package com.zmu.cloud.auth.authentication.config;

import com.zmu.cloud.auth.authentication.service.IResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by zhoutaoo on 2018/5/25.
 */
@Component
class LoadResourceDefine {

    @Autowired
    private IResourceService resourceService;

    /**
     *取消返回的bean防止外部出现线程安全问题
     * 2020/5/15
     * @return
     */
    @PostConstruct
    public void resourceConfigAttributes() {
        resourceService.loadResource();
    }
}
