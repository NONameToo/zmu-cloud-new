package com.zmu.cloud.commons.utils;

import com.alibaba.fastjson.JSON;
import com.zmu.cloud.commons.utils.encryption.Base64Utils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created with IntelliJ IDEA 18.0.1
 *
 * @DESCRIPTION: HttpUtils
 * @Date 2018-11-30 19:53
 */
@Slf4j
public class HttpUtils {

    private static final String DIRECTORY = "/data/temp/download";

    public static RestTemplate restTemplate;
    public static final StringHttpMessageConverter CONVERTER = new StringHttpMessageConverter(StandardCharsets.UTF_8);

    static {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        //设置整个连接池最大连接数 根据自己的场景决定
        connectionManager.setMaxTotal(500);
        //路由是对maxTotal的细分
        connectionManager.setDefaultMaxPerRoute(100);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000) //服务器返回数据(response)的时间，超过该时间抛出read timeout
                .setConnectTimeout(5000)//连接上服务器(握手成功)的时间，超出该时间抛出connect timeout
                //从连接池中获取连接的超时时间，超过该时间未拿到可用连接，
                // 会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
                .setConnectionRequestTimeout(10000)
                .build();
        HttpClient build = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(connectionManager).build();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(build));
        restTemplate.getMessageConverters().set(1, CONVERTER);
    }

    public static <T> T post(String url, Object postData, Class<T> responseType, Map<String, String> headerMap) throws Exception {
        HttpEntity<Object> request = null;
        if (null != headerMap && headerMap.size() > 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.setAll(headerMap);
            request = new HttpEntity<>(postData, headers);
        }
        return restTemplate.postForObject(url, request, responseType);
    }

    public static String post(String url) throws Exception {
        return post(url, null);
    }

    public static <T> T post(String url, Class<T> responseType) throws Exception {
        return post(url, null, responseType);
    }

    public static String post(String url, Object postData) {
        HttpEntity<Object> request = null;
        if (null != postData) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            request = new HttpEntity<>(postData, headers);
        }
        return restTemplate.postForObject(url, request, String.class);
    }


    public static ResponseEntity<String> postForm(String url, Map<String, String> postData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        for (Map.Entry<String, String> entry : postData.entrySet()) {
            map.add(entry.getKey(), entry.getValue());
        }
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return restTemplate.postForEntity(url, request, String.class);
    }

    public static <T> T post(String url, Object postData, Class<T> responseType) throws Exception {
        String result = post(url, postData);
        return JSON.parseObject(result, responseType);
    }

    public static String get(String url) throws Exception {
        return get(url, String.class);
    }

    public static <T> T get(String url, Class<T> responseType) throws Exception {
        return restTemplate.getForObject(url, responseType);
    }

    public static <T> T get(String url, Class<T> responseType, Map<String, String> headerMap) throws Exception {
        HttpEntity<Object> request = null;
        if (null != headerMap && headerMap.size() > 0) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            headers.setAll(headerMap);
            request = new HttpEntity<>(null, headers);
        }
        ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.GET, request, responseType);
        if (exchange.getStatusCode().is2xxSuccessful())
            return exchange.getBody();
        else
            throw new RuntimeException("Request failed with [" + url + "] :" + exchange.getStatusCode().getReasonPhrase());
    }


    /**
     * @Description 通过文件路径上传文件
     * @Date 2018-12-1 001 14:38
     */
    public static String uploadPic(String url, String filePath) throws Exception {
        return upload(url, filePath, null);
    }

    /**
     * @Description 通过文件路径 和 需要的header信息上传文件
     * @Date 2018-12-1 001 14:39
     */
    public static String uploadPic(String url, String filePath, Map<String, String> headerMap) throws Exception {
        return upload(url, filePath, headerMap);
    }

    public static String upload(String url, String filePath, Map<String, String> headerMap) throws Exception {
        // 设置请求头信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("Content-Disposition", "filename=\"" + UUIDUtils.getUUID() + "\"");
        if (headerMap != null && !headerMap.isEmpty())
            headers.setAll(headerMap);
        //设置请求内容
        MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
        form.add("file", new FileSystemResource(filePath));
        HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);
        if (log.isDebugEnabled())
            log.info("准备上传文件到 → url=[{}] headers=[{}]", url, JSON.toJSONString(headers));
        ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, files, Object.class);
        if (responseEntity != null && responseEntity.getStatusCode().is2xxSuccessful()) {
            String result = JSON.toJSONString(responseEntity.getBody());
            log.info("上传文件成功！url=[{}] headers=[{}]\nresponse=[{}]", url, JSON.toJSONString(headers), result);
            return result;
        }
        log.error("上传文件失败！url=[{}] headers=[{}]\nmessage=[{}]"
                , url
                , JSON.toJSONString(headers)
                , responseEntity == null ? "null" : responseEntity.getStatusCode().getReasonPhrase());
        throw new RuntimeException("Upload File Fail");
    }


    /**
     * @Description GET方式下载图片
     * @Date 2018-12-1 001 14:37
     */
    public static File downloadPic(String url) throws Exception {
        return downloadPic(url, null, HttpMethod.GET);
    }

    /**
     * @Description POST方式下载图片
     * @Date 2018-12-1 001 14:38
     */
    public static File downloadPic(String url, String data) throws Exception {
        return downloadPic(url, data, HttpMethod.POST);
    }

    /**
     * @Description 下载并上传图片至指定网址，例如：下载微信二维码并上传到自己的服务器
     * @Date 2018-12-1 001 14:50
     */
    public static String downloadAndUploadPic(String downloadUrl, String uploadUrl) {
        File file = null;
        String result = null;
        try {
            file = downloadPic(downloadUrl);
            result = uploadPic(uploadUrl, file.getPath());
        } catch (Exception e) {
            log.error("下载并上传文件失败，downloadUrl={}，uploadUrl={}", downloadUrl, uploadUrl, e);
        } finally {
            if (file != null)
                file.delete();
        }
        return result;
    }

    /**
     * @Description 通过post方式先下载图片，再上传到指定网址
     * @Date 2018-12-1 001 15:00
     */
    public static String downloadAndUploadPic(String downloadUrl, String uploadUrl, String data) {
        File file = null;
        String result = null;
        try {
            file = downloadPic(downloadUrl, data);
            result = uploadPic(uploadUrl, file.getPath());
        } catch (Exception e) {
            log.error("下载并上传文件失败，downloadUrl={}，uploadUrl={}，data={}", downloadUrl, uploadUrl, data, e);
        } finally {
            if (file != null)
                file.delete();
        }
        return result;
    }

    private static File downloadPic(String url, String data, HttpMethod method) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        String filePath = DIRECTORY + "/" + UUIDUtils.getUUID() + ".jpg";
        File file = null;
        ResponseEntity<byte[]> response = null;
        try {
            File directory = new File(DIRECTORY);
            if (!directory.exists())
                directory.mkdirs();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
            HttpEntity<String> entity;
            if (StringUtils.isNotBlank(data)) {
                entity = new HttpEntity<>(data, headers);
            } else
                entity = new HttpEntity<>(headers);

            response = restTemplate.exchange(url, method, entity, byte[].class);
            if (response != null && response.getStatusCode().is2xxSuccessful()) {
                byte[] imageBytes = response.getBody();
                if (imageBytes.length <= 1024) {//一个图片小于1KB的话应该不是正常的图片了，属于错误信息
                    String result = new String(response.getBody(), StandardCharsets.UTF_8);
                    log.error("下载文件失败！url={}，data={}，method={}，result={}", url, data, method, result);
                    throw new RuntimeException(result);
                }
                file = new File(filePath);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                bos.write(imageBytes);
                bos.flush();
                log.info("下载文件成功！size={}kb，url={}\ndata={}，method={}，result={}", file.length() / 1024,
                        url, data, method, response.getStatusCode().getReasonPhrase());
                return file;
            }
            throw new RuntimeException("Download File Fail");
        } catch (Exception e) {
            log.error("下载文件失败！url={}，data={}，method={}，result={}",
                    url, data, method, response == null ? "null" : response.getStatusCode().getReasonPhrase(), e);
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                if (bos != null)
                    bos.close();
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
            }
        }
    }

    public static String basicAuth(String user, String password) {
        return "Basic " + Base64Utils.encode(user + ":" + password);
    }

    public static Map<String, String> parseRequestParam(String url) {
        Map<String, String> map = new HashMap<>();
        if (!url.contains("?")) {
            return map;
        }
        String[] parts = url.split("\\?", 2);
        if (parts.length < 2) {
            return map;
        }
        String parsedStr = parts[1];
        if (parsedStr.contains("&")) {
            String[] multiParamObj = parsedStr.split("&");
            for (String obj : multiParamObj) {
                parseBasicParam(map, obj);
            }
            return map;
        }
        parseBasicParam(map, parsedStr);
        return map;
    }

    private static void parseBasicParam(Map<String, String> map, String str) {
        String[] paramObj = str.split("=");
        if (paramObj.length < 2) {
            return;
        }
        map.put(paramObj[0], paramObj[1]);
    }

}
