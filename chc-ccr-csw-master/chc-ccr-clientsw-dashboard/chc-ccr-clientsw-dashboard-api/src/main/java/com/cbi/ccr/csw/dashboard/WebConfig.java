package com.cbi.ccr.csw.dashboard;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class WebConfig implements WebMvcConfigurer {
	private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/META-INF/resources/",
			"classpath:/resources/", "classpath:/static/", "classpath:/public/" };

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
	}
	// MEK MEK Merged with StaticConfiguration of CswAllApp.java
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//		registry
//		.addResourceHandler("/ui/**")
//		.addResourceLocations("classpath:/static/").resourceChain(true)
//		.addResolver(new PathResourceResolver() {
//				@Override
//				protected Resource getResource(String resourcePath, Resource location) throws IOException {
//					Resource requestedResource = location.createRelative(resourcePath);
//					return requestedResource.exists() && requestedResource.isReadable() ? requestedResource
//							: new ClassPathResource("/static/index.html");
//				}
//			});
//	}

}
