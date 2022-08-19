package com.lb.test;

import jdk.nashorn.internal.ir.CallNode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;

public class AuthenticationTest {

    SimpleAccountRealm  simpleAccountRealm= new SimpleAccountRealm();

    //Before可以理解为事先，After理解为事后
    @Before
    public void addUser(){
        simpleAccountRealm.addAccount("Mark","123456","admin");
    }

    @Test
    public void testAuthentication(){
        //1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);

        //2.主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("Mark", "123456");
        subject.login(token);

        //subject.isAuthenticated();
        System.out.println("isAuthenticated:"+subject.isAuthenticated());

        subject.checkRole("admin");

        //退出
        //subject.logout();

        //System.out.println("isAuthenticated:"+subject.isAuthenticated());


    }
}
