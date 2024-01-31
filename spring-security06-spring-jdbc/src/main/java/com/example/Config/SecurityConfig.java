package com.example.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@EnableWebSecurity  //开启SpringSecurity 之后会默认注册大量的过滤器servlet filter
@Configuration
public class SecurityConfig {

    @Autowired
    DataSource datasource;

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
                                .requestMatchers("/admin/api").hasRole("admin")//必须有admin角色才能访问到
                                .requestMatchers("/user/api").hasAnyRole("admin", "user")// /user/api:admin、user都是可以访问

                                /* 权限 */
//                                .requestMatchers("/admin/api").hasAuthority("admin:api")//必须有admin:api权限才能访问到
//                                .requestMatchers("/user/api").hasAnyAuthority("admin:api", "user:api")// 有admin:api, user:api权限可以访问

                                /* 匹配模式 */
                                .requestMatchers("/admin/api/?").hasAuthority("admin:api")//必须有admin:api权限才能访问到
                                .requestMatchers("/user/api/my/*").hasAuthority("admin:api")//必须有admin:api权限才能访问到
                                .requestMatchers("/admin/api/**").hasAuthority("admin:api")//必须有admin:api权限才能访问到

                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/app/api").permitAll() //匿名可以访问
                                .requestMatchers("/login").permitAll()
                                .anyRequest().authenticated()
        );

        //现在我们借助异常处理配置一个未授权页面: 实际上是不合理的 我们应该捕获异常信息 通过异常类型来判断是什么异常
        http.exceptionHandling(e -> e.accessDeniedPage("/noAuth"));

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

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager();
        userDetailsManager.setDataSource(datasource);


        // admin用户具有admin, user角色
         UserDetails user1 = User.withUsername("admin").password("admin").roles("admin").build();
         UserDetails user2 = User.withUsername("user").password("user").roles("user").build();

//        UserDetails user1 = User.withUsername("admin").password("admin").authorities("admin:api", "user:api").build();
//        UserDetails user2 = User.withUsername("user").password("user").authorities("user:api").build();

        //在表中创建用户信息
        if (!userDetailsManager.userExists("admin") && !userDetailsManager.userExists("user")) {
            userDetailsManager.createUser(user1);
            userDetailsManager.createUser(user2);
        }


        return userDetailsManager;
    }

    /**
     * PasswordEncoder:加密编码
     * 实际开发中开发环境一般是明文加密 在生产环境中是密文加密 也就可以可以配置多种加密方式
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}