package com.chai.yygh.user.api;

import com.chai.yygh.common.result.Result;
import com.chai.yygh.common.util.AuthContextHolder;
import com.chai.yygh.model.user.UserInfo;
import com.chai.yygh.user.service.UserInfoService;
import com.chai.yygh.user.utils.IpUtil;
import com.chai.yygh.vo.user.LoginVo;
import com.chai.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @program: yygh
 * @author:
 * @create: 2024-04-16 21:55
 */
@RestController
@RequestMapping("/api/user")
public class UserInfoApiController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "会员登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginVo loginVo, HttpServletRequest request) {
        loginVo.setIp(IpUtil.getIpAddr(request));
        Map<String, Object> info = userInfoService.login(loginVo);
        return Result.ok(info);
    }

    //用户认证接口
    @PostMapping("auth/userAuth")
    public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
        //传递两个参数，第一个参数用户id，第二个参数认证数据vo对象
        userInfoService.userAuth(AuthContextHolder.getUserId(request),userAuthVo);
        return Result.ok();
    }

    //获取用户id信息接口
    @GetMapping("auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = AuthContextHolder.getUserId(request);
        UserInfo userInfo = userInfoService.getById(userId);
        return Result.ok(userInfo);
    }
}
