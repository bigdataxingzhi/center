package com.core.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * 基于角色的登录入口控制器
 * @author 星志
 *
 */
public class LoginUrlEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private static final String API_FREFIX = "/api";
    private static final String API_CODE_403 = "{\"code\": 403}";
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";

    private PathMatcher pathMatcher = new AntPathMatcher();


    public LoginUrlEntryPoint(String loginFormUrl) {
        super(loginFormUrl);

    }

    /*
     * 根据请求跳转到指定的页面，父类是默认使用loginFormUrl
     * @param request
     * @param response
     * @param exception
     * @return
     */
    @Override
    protected String determineUrlToUseForThisRequest(HttpServletRequest request, HttpServletResponse response,
                                                     AuthenticationException exception) {


        return "/userLogin";
    }

    /*
     * 如果是Api接口 返回json数据 否则按照一般流程处理
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (uri.startsWith(API_FREFIX)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(CONTENT_TYPE);

            PrintWriter pw = response.getWriter();
            pw.write(API_CODE_403);
            pw.close();
        } else {
            super.commence(request, response, authException);
        }

    }
}
