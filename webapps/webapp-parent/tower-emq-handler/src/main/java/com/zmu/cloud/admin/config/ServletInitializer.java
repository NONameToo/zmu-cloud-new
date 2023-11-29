package com.zmu.cloud.admin.config;

import com.zmu.cloud.admin.TowerEmqHandleApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author YH
 */
public class ServletInitializer extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(TowerEmqHandleApplication.class);
	}

}
