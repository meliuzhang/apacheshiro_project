package com.lb.apacheshiro.service;


import com.lb.apacheshiro.model.User;

public interface UserService {

    User findByUsername(String username);
}
