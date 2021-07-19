package com.xingkaichun.helloworldblockchain.application.configuration;

import com.xingkaichun.helloworldblockchain.application.interceptor.IpInterceptor;
import com.xingkaichun.helloworldblockchain.application.vo.framwork.ServiceResult;
import com.xingkaichun.helloworldblockchain.util.JsonUtil;
import com.xingkaichun.helloworldblockchain.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


/**
 *
 * @author 邢开春 409060350@qq.com
 */
@Configuration
public class WebMvcConfigurerConfiguration implements WebMvcConfigurer {

	@Autowired
	private IpInterceptor ipInterceptor;


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(ipInterceptor).addPathPatterns("/**");
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> handlerExceptionResolvers) {
		handlerExceptionResolvers.add((httpServletRequest, httpServletResponse, handler, exception) -> {
			LogUtil.error("统一异常拦截。",exception);
			try {
				httpServletResponse.setHeader("Content-type", "application/json;");
				httpServletResponse.setStatus(500);
				httpServletResponse.setCharacterEncoding("UTF-8");
				ServiceResult serviceResult = ServiceResult.createFailServiceResult(exception.getMessage());
				String jsonServiceResult = JsonUtil.toString(serviceResult);
				httpServletResponse.getWriter().write(jsonServiceResult);
			} catch (Exception e) {
				LogUtil.error("将统一异常写入到HttpServletResponse出现错误。",e);
			}
			return new ModelAndView();
		});
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("*")
				.allowCredentials(true)
				.allowedMethods("*")
				.maxAge(3600);
	}
}