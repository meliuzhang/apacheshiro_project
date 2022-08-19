package com.lb.apacheshiro.service;

import com.lb.apacheshiro.mapper.UserMapper;
import com.lb.apacheshiro.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}
