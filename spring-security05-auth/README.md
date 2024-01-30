# 认证和授权
## 授权
1. 角色
2. 权限

角色、权限在SpringSecurity都是一样的, 只不过从业务上做了区分
他们都是我们指定的标志

`/admin/user/save:`\
    角色:admin【拥有admin角色的用户可以访问】\
    权限:user:save【拥有user:save权限的用户可以执行保存】

`/admin/user/query:` \
    角色:user【拥有user角色的用户可以访问】\
    权限:user:query【拥有user:query权限的用户可以执行查询】

在实际的应用中:往往\
菜单:通过角色来控制\
权限:控制接口的访问

1、配置角色/权限\
2、将用户存储到内存中\
/admin/api:需要admin角色访问\
/user/api:需要user角色访问\
/app/api:匿名可以访问
