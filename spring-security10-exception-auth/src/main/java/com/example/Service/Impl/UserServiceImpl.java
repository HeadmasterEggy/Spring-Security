package com.example.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.Entity.Perm;
import com.example.Entity.User;
import com.example.Service.UserService;
import com.example.mapper.PermMapper;
import com.example.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    UserMapper userMapper;

    @Resource
    PermMapper permMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户未找到");
        }

        //根据用户名查找权限
        QueryWrapper<Perm> permQueryWrapper = new QueryWrapper<>();
        permQueryWrapper.eq("user_id", user.getId());

        List<Perm> perms = permMapper.selectList(permQueryWrapper);

        List<String> permTags = perms.stream().map(Perm::getTag).toList();

        user.setAuthorities(AuthorityUtils.createAuthorityList(permTags));


        return user;
    }
}
