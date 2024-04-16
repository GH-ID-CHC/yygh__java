package com.chai.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chai.yygh.common.exception.YyghException;
import com.chai.yygh.common.result.ResultCodeEnum;
import com.chai.yygh.model.acl.User;
import com.chai.yygh.model.user.UserInfo;
import com.chai.yygh.user.mapper.UserInfoMapper;
import com.chai.yygh.user.service.UserInfoService;
import com.chai.yygh.vo.user.LoginVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: yygh
 * @author:
 * @create: 2024-04-16 21:56
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String code = loginVo.getCode();
        String phone = loginVo.getPhone();
        if (StringUtils.isEmpty(phone) || phone==null){
            throw new YyghException(ResultCodeEnum.PERMISSION);
        }

        //判断手机号和输入的验证码是否一致

        //判断是否是第一次登录
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone",phone);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        if (userInfo==null){
//            第一次登录
            userInfo=new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }

        //校验用户是否被禁用
        if (userInfo.getAuthStatus()==0){
            throw new YyghException(ResultCodeEnum.LOGIN_ACL);
        }

        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        map.put("token", "");
        return map;
    }
}