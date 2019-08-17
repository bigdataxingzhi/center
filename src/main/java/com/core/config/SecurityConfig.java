package com.core.config;


import com.core.security.AuthenticationSuccessHandler;
import com.core.security.LoginAuthFailHandler;
import com.core.security.LoginUrlEntryPoint;
import com.core.service.DefaultUserDetailsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.sql.DataSource;

/**
 * 
 * @author 星志
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	SessionInformationExpiredStrategy mySessionInformationExpiredStrategy;
	
	  @Autowired
	    DataSource dataSource;
	  
	    /*
	     * 默认密码处理器
	     * @return
	     */
	    @Bean
	    @ConditionalOnMissingBean(PasswordEncoder.class)
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

	 

	   /**
	     * 默认认证器
	     *
	     * @return
	     */
	    @Bean
	    @ConditionalOnMissingBean(UserDetailsService.class)
	    public UserDetailsService userDetailsService() {
	        return new DefaultUserDetailsService();
	    }
	  
	  
	  @Bean
	    public PersistentTokenRepository persistentTokenRepository(){
	    	JdbcTokenRepositoryImpl toRepository = new JdbcTokenRepositoryImpl();
	    	toRepository.setDataSource(dataSource);
	    	return toRepository;
	    }
    /**
     * HTTP权限控制
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 资源访问权限
        http.authorizeRequests()
               .antMatchers("/static/**").permitAll() // 静态资源
                .antMatchers("templates/**").permitAll()
                .antMatchers("/manager/findPage").authenticated()
                .antMatchers("/manager/findOne").authenticated()
                .antMatchers("/manager/update").authenticated()
                .antMatchers("/manager/delete").authenticated()
                .antMatchers("/manager/save").authenticated()
                .antMatchers("/manager/save").authenticated()
                .antMatchers("/add.html").authenticated()
                .antMatchers("/brand.html").authenticated()
                .antMatchers("/index.html").authenticated()
                .antMatchers("/index").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()//配置表单登陆
                .loginPage("/login")
                .loginProcessingUrl("/login") // 配置角色登录处理入口,即表单提交的action
                .failureHandler(authFailHandler())
                .successHandler(authSuccessHandler())
                .and()
                .exceptionHandling()
               // .authenticationEntryPoint(urlEntryPoint())
                .accessDeniedPage("/403")
                .and()
                .logout()
                .logoutUrl("/logout")//退出url
                .logoutSuccessUrl("/main.html")
                .deleteCookies("JSESSIONID")//退出时使session失效,清除cookie.
                .invalidateHttpSession(true);
            http.
             sessionManagement()
             .maximumSessions(1)
             .expiredSessionStrategy(new MyExpiredSessionStrategy());
            http.rememberMe().
            tokenRepository(persistentTokenRepository())
            .tokenValiditySeconds(3600)
            .userDetailsService(userDetailsService()).authenticationSuccessHandler(authSuccessHandler());

        http.csrf().disable(); //关闭csrf
        http.headers().frameOptions().sameOrigin(); //开启同源策略.如前端使用iframe开发时.
    }

    @Bean
    public LoginUrlEntryPoint urlEntryPoint() {
        return new LoginUrlEntryPoint("/userLogin");
    }

    //登陆失败处理器.
    @Bean
    public LoginAuthFailHandler authFailHandler() {
        return new LoginAuthFailHandler(urlEntryPoint());
    }

    @Bean
    public AuthenticationSuccessHandler authSuccessHandler() {
        return new AuthenticationSuccessHandler();
    }
    
    
}
