package com.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import com.core.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import scala.annotation.meta.setter;

/**
 * 
 * @author 星志
 *
 */
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Value("${singInSuccessUrl}")
    private String singInSuccessUrl;

    @Autowired
    private ObjectMapper objectMapper;

  


    private RequestCache requestCache = new HttpSessionRequestCache();
    


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

    	 User principal = (User) authentication.getPrincipal();
        request.getSession().setAttribute("user", principal);

            // 如果设置了singInSuccessUrl，总是跳到设置的地址上
            // 如果没设置，则尝试跳转到登录之前访问的地址上，如果登录前访问地址为空，则跳到网站根路径上
            if (StringUtils.isNotBlank(singInSuccessUrl)) {
                requestCache.removeRequest(request, response);
                setAlwaysUseDefaultTargetUrl(true);
                setDefaultTargetUrl(singInSuccessUrl);
            }
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }


