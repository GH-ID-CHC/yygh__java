package com.chai.yygh.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chai.yygh.model.user.UserInfo;
import com.chai.yygh.vo.user.LoginVo;
import com.chai.yygh.vo.user.UserAuthVo;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {

    /**
     * 登录返回用户信息
     *
     * @param loginVo 登录签证官
     * @return {@link Map}<{@link String}, {@link Object}>
     */
    Map<String, Object> login(LoginVo loginVo);

    /**
     * 根据微信openid获取用户信息
     *
     * @param openId 开放id
     * @return {@link UserInfo}
     */
    UserInfo getByOpenid(String openId);


    void userAuth(Long userId, UserAuthVo userAuthVo);
}
