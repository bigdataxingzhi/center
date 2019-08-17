package com.core.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/**
 * 登录验证失败处理器.
 * 
 */
public class LoginAuthFailHandler extends SimpleUrlAuthenticationFailureHandler {
    private final LoginUrlEntryPoint urlEntryPoint;

    public LoginAuthFailHandler(LoginUrlEntryPoint urlEntryPoint) {
        this.urlEntryPoint = urlEntryPoint;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,AuthenticationException exception) throws IOException, ServletException {

      //获取登陆失败后,原先表单的url
     String targetUrl = this.urlEntryPoint.determineUrlToUseForThisRequest(request, response, exception);
    targetUrl += "?" + exception.getMessage();
    //设置跳转的url.并携带异常信息.
        super.setDefaultFailureUrl(targetUrl);
        //对父类的onAuthenticationFailure的说明==>在request中保存异常信息,并优先转发至defaultFailureUrl

        /**
          if (defaultFailureUrl == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Authentication Failed: " + exception.getMessage());
         }else{
           saveException(request, exception);
             if (forwardToDestination) {
                request.getRequestDispatcher(defaultFailureUrl).forward(request, response);
             }else {
                redirectStrategy.sendRedirect(request, response, defaultFailureUrl);
             }
         }
         */
        super.onAuthenticationFailure(request, response, exception);

}
}
