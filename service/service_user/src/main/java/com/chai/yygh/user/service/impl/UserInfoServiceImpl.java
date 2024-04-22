package com.chai.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chai.yygh.common.exception.YyghException;
import com.chai.yygh.common.helper.JwtHelper;
import com.chai.yygh.common.result.ResultCodeEnum;
import com.chai.yygh.model.acl.User;
import com.chai.yygh.model.user.UserInfo;
import com.chai.yygh.user.mapper.UserInfoMapper;
import com.chai.yygh.user.service.UserInfoService;
import com.chai.yygh.vo.user.LoginVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.management.Query;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;
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

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        String code = loginVo.getCode();
        String phone = loginVo.getPhone();
        if (StringUtils.isEmpty(phone) || phone == null) {
            throw new YyghException(ResultCodeEnum.PERMISSION);
        }

        //校验校验验证码
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }

        //绑定手机号码
        UserInfo userInfo = null;
        if(!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.getByOpenid(loginVo.getOpenid());
            if(null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);
            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
        }
        if(null == userInfo) {
            //判断是否是第一次登录
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {
//            第一次登录
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
        }


        //校验用户是否被禁用
        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_ACL);
        }

        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);

        //生成token
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    @Override
    public UserInfo getByOpenid(String openId) {
        return userInfoMapper.selectOne(new QueryWrapper<UserInfo>().eq("openid", openId));
    }
}