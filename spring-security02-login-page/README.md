# 认证过程源码分析

1. 在哪里做登录 filter
2. 用户是存储在哪里的 我们是不是可以自己添加多个用户呢???
3. 退出接口 在哪里实现的???

启动项目的时候，会执行`SecurityConfig.securityFilterchain => formLogin`

`new FormLoginConfigurer<>()`

```java
public FormLoginConfigurer() {
    super(new UsernamePasswordAuthenticationFilter(), null);
    usernameParameter("username");
    passwordParameter("password");
}
```
如果我们想自定义一个登录接口/filter:继承AbstractAuthenticationProcessingFilter

```java

@Bean
public InMemoryUserDetailsManager inMemoryUserDetailsManager(SecurityProperties properties,
                                                             ObjectProvider<PasswordEncoder> passwordEncoder) {
    SecurityProperties.User user = properties.getUser();
    List<String> roles = user.getRoles();
    return new InMemoryUserDetailsManager(User.withUsername(user.getName())
            .password(getOrDeducePassword(user, passwordEncoder.getIfAvailable()))
            .roles(StringUtils.toStringArray(roles))
            .build());
}
```

```java
//如果我们需要查询数据库 则实现这个接口 替换默认的InMemoryUserDetailsManager
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```
