package com.lb.apacheshiro;

import com.lb.apacheshiro.model.Permission;
import com.lb.apacheshiro.model.Role;
import com.lb.apacheshiro.model.User;
import com.lb.apacheshiro.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author LB
 * @Remarks
 * @date 2019/12/19 21:58
 */
public class AuthRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //从session 中获取用户
        User user = (User) principals.fromRealm(this.getClass().getName()).iterator().next();
        //权限存入这个list
        List<String> permissionList = new ArrayList<>();
        //角色存入这个list
        List<String> roleNameList = new ArrayList<>();
        //set集合 无序 不允许重复 没有索引
        //取出用户的角色
        Set<Role> roleSet = user.getRoles();
        //角色不为空
        if(CollectionUtils.isNotEmpty(roleSet)){
            for (Role role : roleSet) {
                //角色名称
                roleNameList.add(role.getRname());
                //通过角色获取权限
                Set<Permission> permissions = role.getPermissions();
                if(CollectionUtils.isNotEmpty(permissions)){
                    for (Permission permission : permissions) {
                        permissionList.add(permission.getName());
                    }
                }
            }
        }
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //添加权限
        info.addStringPermissions(permissionList);
        //添加角色
        info.addRoles(roleNameList);
        return info;
    }

    //认证登陆
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String username = usernamePasswordToken.getUsername();
        User user = userService.findByUsername(username);
        return new  SimpleAuthenticationInfo(user,user.getPassword(),this.getClass().getName());
    }
}
