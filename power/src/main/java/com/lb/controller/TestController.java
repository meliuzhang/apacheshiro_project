package com.lb.controller;


import com.lb.common.ApplicationContextHelper;
import com.lb.common.JsonData;
import com.lb.dao.SysAclModuleMapper;
import com.lb.exception.ParamException;
import com.lb.exception.PermissionException;
import com.lb.model.SysAclModule;
import com.lb.param.TestVo;
import com.lb.util.BeanValidator;
import com.lb.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {

    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello() {
        log.info("hello");
        throw new PermissionException("test exception");
        // return JsonData.success("hello, permission");
    }

//    @RequestMapping("/validate.json")
//    @ResponseBody
//    public JsonData validate(TestVo vo) throws ParamException {
//        log.info("validate");
//        BeanValidator.check(vo);
//        return JsonData.success("test validate");
//    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate() throws ParamException {
        log.info("validate");
        //通过上下文对象获取SysAclModuleMapper
        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        //调用方法查询数据库
        SysAclModule module = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.obj2String(module));
        return JsonData.success(module);
    }
}
