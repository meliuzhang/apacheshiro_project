package com.lb.apacheshiro;

import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.util.LinkedHashMap;

/**
 * @author LB
 * @Remarks
 * @date 2019/12/19 22:25
 */
@Configuration
public class ShiroConfiguration {

    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(@Qualifier("securityManager") SecurityManager manager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(manager); //securityManager存入ShiroFilterFactoryBean

        bean.setLoginUrl("/login");//定义登陆的url
        bean.setSuccessUrl("/index");//定义登陆成功之后跳转的url
        bean.setUnauthorizedUrl("/unauthorized");//定义没有权限访问跳转的url

        //配置请求如何拦截
        LinkedHashMap<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // key:拦截什么请求
        //vlue: 用什么拦截器（什么权限才可以访问）
        filterChainDefinitionMap.put("/index", "authc");//authc 登陆之后才可以访问
        filterChainDefinitionMap.put("/login", "anon"); //anon 不用登陆也可以访问
        filterChainDefinitionMap.put("/loginUser", "anon");
        filterChainDefinitionMap.put("/admin", "roles[admin]");   //角色是admin才可以访问
        filterChainDefinitionMap.put("/edit", "perms[edit]");     //拥有 edit权限才可以访问
        filterChainDefinitionMap.put("/druid/**", "anon");
        filterChainDefinitionMap.put("/**", "user");
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return bean;
    }

    //securityManager
    @Bean("securityManager")
    public SecurityManager securityManager(@Qualifier("authRealm") AuthRealm authRealm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(authRealm);//授权认证存入securityManager
        return manager;
    }

    //授权认证
    @Bean("authRealm")
    public AuthRealm authRealm(@Qualifier("credentialMatcher") CredentialMatcher matcher) {
        AuthRealm authRealm = new AuthRealm();
       // authRealm.setCacheManager(new MemoryConstrainedCacheManager());
        authRealm.setCredentialsMatcher(matcher);//自定义的密码校验规则存入AuthRealm（授权认证）
        return authRealm;
    }

    //自定义的密码校验规则
    @Bean("credentialMatcher")
    public CredentialMatcher credentialMatcher() {
        return new CredentialMatcher();
    }

    //配置shiro和spring的关联
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(@Qualifier("securityManager") SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }
}
