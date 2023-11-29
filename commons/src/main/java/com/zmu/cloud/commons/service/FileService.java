package com.zmu.cloud.commons.service;


import com.zmu.cloud.commons.dto.commons.oss.FileResourceResponse;
import com.zmu.cloud.commons.dto.commons.oss.OssCallbackResponse;
import com.zmu.cloud.commons.dto.commons.oss.OssSignResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


public interface FileService {

    @ApiOperation("查询上传文件签名")
    @GetMapping("/findOssSign")
    OssSignResponse findOssSign();

    @ApiOperation("上传文件回调验证")
    @PostMapping("/verifyOSSCallback")
    FileResourceResponse verifyOSSCallback(@RequestBody OssCallbackResponse ossCallbackDto);

}