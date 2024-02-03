package com.example.Config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@EnableWebSecurity  //开启SpringSecurity 之后会默认注册大量的过滤器servlet filter
@Configuration
public class SecurityConfig {

    /**
     * PasswordEncoder:加密编码
     * 实际开发中开发环境一般是明文加密 在生产环境中是密文加密 也就可以可以配置多种加密方式
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // login登录页面需要匿名访问
        // permitAll: 具有所有权限 也就可以匿名可以访问
        // authorizeHttpRequests:针对http请求进行授权配置
        // anyRequest:任何请求 所有请求
        // authenticated:认证【登录】
        http.authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests

                                /* 角色 */
//                        .requestMatchers("/admin/api").hasRole("admin")//必须有admin角色才能访问到
//                        .requestMatchers("/user/api").hasAnyRole("admin", "user")// /user/api:admin、user都是可以访问

                                /* 权限 */
                                .requestMatchers("/admin/api").hasAuthority("admin:api")//必须有admin:api权限才能访问到
                                .requestMatchers("/user/api").hasAnyAuthority("admin:api", "user:api")// 有admin:api, user:api权限可以访问

                                /* 匹配模式 */
                                .requestMatchers("/admin/api/?").hasAuthority("admin:api")//必须有admin:api权限才能访问到
                                .requestMatchers("/user/api/my/*").hasAuthority("admin:api")//必须有admin:api权限才能访问到
                                .requestMatchers("/admin/api/**").hasAuthority("admin:api")//必须有admin:api权限才能访问到

                                .requestMatchers("/app/api").permitAll() //匿名可以访问
                                .requestMatchers("/login").permitAll()
                                .anyRequest().authenticated()
        );

        //现在我们借助异常处理配置一个未授权页面: 实际上是不合理的 我们应该捕获异常信息 通过异常类型来判断是什么异常
        //http.exceptionHandling(e -> e.accessDeniedPage("/noAuth"));
        http.exceptionHandling(e -> e.accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                System.out.println("accessDeniedException = " + accessDeniedException);
                accessDeniedException.printStackTrace();
            }
        }));
        // http:后面可以一直点 但是太多内容之后不美观
        // loginPage:登录页面
        // loginProcessingUrl:登录接口 过滤器
        // defaultSuccessUrl:登录成功后访问的页面
        http.formLogin(formLogin ->
                formLogin
                        .loginPage("/login").permitAll()
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/index")
        );

        // Customizer.withDefaults(): 关闭
        // http.csrf(csrf -> csrf.disable());
        http.csrf(Customizer.withDefaults());   //跨域漏洞防御

        // 退出
        http.logout(logout -> logout.invalidateHttpSession(true));

        return http.build();
    }


}
