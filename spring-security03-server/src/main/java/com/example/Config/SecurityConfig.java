package com.example.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity  //开启SpringSecurity 之后会默认注册大量的过滤器servlet filter
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // login登录页面需要匿名访问
        // permitAll: 具有所有权限 也就可以匿名可以访问
        // authorizeHttpRequests:针对http请求进行授权配置
        // anyRequest:任何请求 所有请求
        // authenticated:认证【登录】
        http.authorizeHttpRequests(authorizeHttpRequests ->
                authorizeHttpRequests
                        .requestMatchers("/login")
                        .permitAll()
                        .anyRequest().authenticated()
        );

        // http:后面可以一直点 但是太多内容之后不美观
        // loginProcessingUrl:指定登录接口
        // successHandler: 登录成功处理器
        // failureHandler: 登录失败处理器
        http.formLogin(formLogin ->
                formLogin
                        .loginProcessingUrl("/login").permitAll()
                        .successHandler((request, response, authentication) -> {
                            response.setContentType("text/html;charset=UTF-8");
                            response.getWriter().write("loginOK");

                            System.out.println("authentication.getCredentials() = " + authentication.getCredentials());
                            System.out.println("authentication.getPrincipal() = " + authentication.getPrincipal());
                            System.out.println("authentication.getAuthorities() = " + authentication.getAuthorities());
                        })
                        .failureHandler((request, response, exception) -> {
                            response.setContentType("text/html;charset=UTF-8");
                            response.getWriter().write("loginERROR");
                            System.out.println("登录信息异常");
                            exception.printStackTrace();
                        })
        );

        // Customizer.withDefaults(): 关闭
        // http.csrf(csrf -> csrf.disable());
        http.csrf(Customizer.withDefaults());   //跨域漏洞防御

        //跨域拦截关闭
        http.cors(Customizer.withDefaults());


        return http.build();
    }

}
