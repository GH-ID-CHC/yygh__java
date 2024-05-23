package com.chai.yygh.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chai.yygh.model.user.UserInfo;
import com.chai.yygh.vo.user.LoginVo;
import com.chai.yygh.vo.user.UserAuthVo;
import com.chai.yygh.vo.user.UserInfoQueryVo;

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

    //用户列表（条件查询带分页）
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    /**
     * 用户锁定
     * @param userId
     * @param status 0：锁定 1：正常
     */
    void lock(Long userId, Integer status);
}
