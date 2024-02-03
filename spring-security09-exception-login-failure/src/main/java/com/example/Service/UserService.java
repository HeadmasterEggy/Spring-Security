package com.example.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.Entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends IService<User>, UserDetailsService {
}
