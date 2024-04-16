package com.chai.yygh.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chai.yygh.model.user.UserInfo;
import com.chai.yygh.user.mapper.UserInfoMapper;
import com.chai.yygh.user.service.UserInfoService;
import org.springframework.stereotype.Service;

/**
 * @program: yygh
 * @author:
 * @create: 2024-04-16 21:56
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
}