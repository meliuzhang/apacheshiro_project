package com.lb.shiro.realm;

import jdk.nashorn.internal.ir.CallNode;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomRealm extends AuthorizingRealm {

    Map<String,String> userMap = new HashMap<>(16);
    {
        userMap.put("Mark","c69e364ff01a7988ab3813746d0e61d2");
    }

    /**
     * 授权
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取用户信息
        String userName =(String) principalCollection.getPrimaryPrincipal();
        //根据用户信息获取用户拥有的角色
        Set<String> roles  = getRolesByUserName(userName);
        //根据用户信息获取用户拥有的权限
        Set<String> permissions = getPermissionsByUsername(userName);
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //添加权限
        simpleAuthorizationInfo.setStringPermissions(permissions);
        //添加角色
        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }

    private Set<String> getPermissionsByUsername(String userName) {
        Set<String> sets = new HashSet<>();
        sets.add("user:delete");
        sets.add("user:add");
        return sets;
    }

    private Set<String> getRolesByUserName(String userName) {
        Set<String> sets = new HashSet<>();
        sets.add("admin");
        return sets;
    }

    /**
     * 登录认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //1.从主体传过来的认证信息中获取用户名
        String userName = (String)authenticationToken.getPrincipal();

        //2.通过用户名到数据库中获取凭证
        String password = getPasswordByUserName(userName);

        if(password == null){
            return null;
        }

        /**
         * 参数1：数据库查询到的userName
         * 参数2：数据库查询到的password
         * 参数3：该类的类名，可以使用this.getClass().getName()获得
         */
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(userName,password,this.getClass().getName());

        // 设置加密的 盐
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("cheng"));

        return simpleAuthenticationInfo;
    }

    //模拟访问数据库
    private String getPasswordByUserName(String userName) {
        return userMap.get(userName);
    }

    public static void main(String[] args) {
        // 密码 + 盐 加密后的结果
        Md5Hash md5Hash = new Md5Hash("123456", "cheng");
        System.out.println(md5Hash.toString());
    }
}
