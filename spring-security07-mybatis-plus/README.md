源码分析技巧:
1. debug回退
2. 制造异常: 查看堆栈调用链

在项目启动的时候将UserDetailsService实现类配置到 DaoAuthenticationprovider 并且添加到Spring Applicatiocontext容器中

- 在我们登录的时候走到过滤器UsernamePasswordAuthenticationFilter:
- 在这个过滤器中获取DaoAuthenticationProvider.retrieveUser
- this.getUserDetailsService().loadUserByUsername(username)
