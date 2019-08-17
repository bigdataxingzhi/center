package com.core.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

@Component
public class MyExpiredSessionStrategy implements SessionInformationExpiredStrategy {

	@Override
	public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {

		HttpServletResponse response = event.getResponse();
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		response.getWriter().write("<h1>"+"您已经在别处登录，如果非本人操作，请修改您的密码！！"+"</h1>");
	}

}
