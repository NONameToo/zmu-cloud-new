package com.zmu.cloud.commons.controller;

import com.aliyun.oss.OSSClient;
import com.zmu.cloud.commons.annotations.NoAuth;
import com.zmu.cloud.commons.config.AliyunOSSWebConfig;
import com.zmu.cloud.commons.constants.CommonsConstants;
import com.zmu.cloud.commons.dto.commons.oss.FileResourceResponse;
import com.zmu.cloud.commons.dto.commons.oss.OssCallbackResponse;
import com.zmu.cloud.commons.dto.commons.oss.OssSignResponse;
import com.zmu.cloud.commons.exception.BaseException;
import com.zmu.cloud.commons.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

/**
 * (FileResource)表控制层
 *
 * @author makejava
 * @since 2018-12-11 14:12:44
 */
@Api(tags = "文件上传")
@RestController
@Slf4j
@RequestMapping({CommonsConstants.API_PREFIX + "/file", CommonsConstants.ADMIN_PREFIX + "/file"})
public class FileController extends BaseController {

    @Autowired
    private FileService fileService;
    @Autowired
    private AliyunOSSWebConfig ossInfo;

    @GetMapping("/ossSign")
    @ApiOperation("获取上传文件签名")
    public OssSignResponse ossSign() {
        return fileService.findOssSign();
    }

    @PostMapping("/verifyOSSCallback")
    @NoAuth
    @ApiIgnore
    public FileResourceResponse verifyOSSCallback(HttpEntity<String> httpEntity, HttpServletRequest request) {
        String ossCallbackBody = httpEntity.getBody();
        String authorization = request.getHeader("authorization");
        String pubKeyInput = request.getHeader("x-oss-pub-key-url");
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        OssCallbackResponse ossCallbackDto = new OssCallbackResponse(authorization, pubKeyInput, uri, queryString, ossCallbackBody);
        return fileService.verifyOSSCallback(ossCallbackDto);
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "阿里云上传单个文件", notes = "上传单个文件")
    @ApiIgnore
    public String handleFileUploadSingle(@ApiParam(name = "file", value = "文件数据流") MultipartFile file) {
        if (file == null || file.isEmpty() || org.apache.commons.lang3.StringUtils.isBlank(file.getOriginalFilename()))
            throw new BaseException("文件或文件名不可为空");
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (filename.contains("..")) {
                log.error("Cannot store file with relative path outside current directory " + filename);
                throw new BaseException("文件名错误");
            }
            String originalFilename = file.getOriginalFilename();
            String filetype = "";
            if (org.apache.commons.lang3.StringUtils.isNotBlank(originalFilename) && originalFilename.lastIndexOf(".") > 0)
                filetype = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String key = bucketType(filetype) + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + UUID.randomUUID() + "." + filetype;
            String domain = ossInfo.getBaseUrl();
            doUpload(key, file.getInputStream());
            String path = domain + key;
            log.info("uploadFile success ... fileName={}，size={} kb，path={}", filename, file.getSize() / 1024L, path);
            return path;
        } catch (Exception ce) {
            log.error("上传:" + filename + "失败:" + ce.getMessage());
            throw new BaseException("上传文件失败");
        }
    }

    void doUpload(String key, InputStream inputStream) {
        OSSClient ossClient = null;
        try {
            ossClient = new OSSClient(ossInfo.getEndpoint(), ossInfo.getAccessKeyId(), ossInfo.getAccessKeySecret());
            //文件流上传
            ossClient.putObject(ossInfo.getBucket(), key, inputStream);
        } catch (Exception e) {
            log.error("上传文件到阿里云oss失败：", e);
            throw new RuntimeException(e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    static String bucketType(String sufix) {
        // 后期提取为配置文件
        if (sufix.equalsIgnoreCase("jpg")
                || sufix.equalsIgnoreCase("jpeg")
                || sufix.equalsIgnoreCase("bmp")
                || sufix.equalsIgnoreCase("gif")
                || sufix.equalsIgnoreCase("png")) {
            return "pic";
        } else if (sufix.equalsIgnoreCase("mp3")
                || sufix.equalsIgnoreCase("aac")
                || sufix.equalsIgnoreCase("amr")
                || sufix.equalsIgnoreCase("m4a")
                || sufix.equalsIgnoreCase("m4r")
                || sufix.equalsIgnoreCase("wav")
        ) {
            return "audio";
        } else if (sufix.equalsIgnoreCase("mp4")
                || sufix.equalsIgnoreCase("avi")
                || sufix.equalsIgnoreCase("mkv")
                || sufix.equalsIgnoreCase("mov")
                || sufix.equalsIgnoreCase("mpeg")
                || sufix.equalsIgnoreCase("wmv")) {
            return "video";
        }
        return "commons";
    }
}