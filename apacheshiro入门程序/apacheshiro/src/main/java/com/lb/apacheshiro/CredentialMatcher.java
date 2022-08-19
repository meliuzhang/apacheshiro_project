package com.lb.apacheshiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * @author LB
 * @Remarks
 * @date 2019/12/19 22:21
 */
public class CredentialMatcher extends SimpleCredentialsMatcher {

    /**
     * 密码校验规则重写
     * @param token
     * @param info
     * @return
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
        //用户输入的密码
        String password = new String(usernamePasswordToken.getPassword());
        //数据库里的密码
        String dbPassword = (String) info.getCredentials();
        return this.equals(password, dbPassword);
    }
}
