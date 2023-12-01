package com.zmu.cloud.commons.config;

import com.aliyun.oss.OSSClient;
import java.io.InputStream;

/**
 * 阿里云OSS基础bean
 *
 * @author liyi
 * @create 2018-01-18 13:36
 **/
public class AliyunOSS {

    private String id;

    private String secret;

    private String bucket;

    private String baseUrl;

    private String endpoint;

    public static class Builder{
        private String id;

        private String secret;

        private String bucket;

        private String baseUrl;

        private String endpoint;

        public Builder setId(String id){
            this.id = id;
            return this;
        }

        public Builder setSecret(String secret){
            this.secret = secret;
            return this;
        }

        public Builder setBucket(String bucket){
            this.bucket = bucket;
            return this;
        }

        public Builder setBaseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder setEndpoint(String endpoint){
            this.endpoint = endpoint;
            return this;
        }

        public AliyunOSS build(){
            return new AliyunOSS(this);
        }
    }

    private AliyunOSS(Builder builder){
        this.id = builder.id;
        this.baseUrl = builder.baseUrl;
        this.endpoint = builder.endpoint;
        this.bucket = builder.bucket;
        this.secret = builder.secret;
    }

    public static Builder options(){
        return new Builder();
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getId() {
        return id;
    }

    public String getSecret() {
        return secret;
    }

    public String getBucket() {
        return bucket;
    }

    public String upload(InputStream in,String filename){
        // endpoint以杭州为例，其它region请按实际情况填写
        String endpoint = this.endpoint;
        // accessKey请登录https://ak-console.aliyun.com/#/查看
        String accessKeyId = this.id;
        String accessKeySecret = this.secret;
        // 创建OSSClient实例
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // 上传文件流
        ossClient.deleteObject(this.bucket, filename);
        ossClient.putObject(this.bucket, filename, in);
        // 关闭client
        ossClient.shutdown();
        return this.baseUrl+filename;
    }
}
