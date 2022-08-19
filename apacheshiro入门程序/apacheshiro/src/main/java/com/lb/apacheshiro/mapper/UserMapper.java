package com.lb.apacheshiro.mapper;

import com.lb.apacheshiro.model.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    User findByUsername(@Param("username") String username);
}
