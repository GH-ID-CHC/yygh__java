package com.chai.yygh.controller.api;

import com.chai.yygh.common.exception.YyghException;
import com.chai.yygh.common.result.Result;
import com.chai.yygh.common.result.ResultCodeEnum;
import com.chai.yygh.model.hosp.Department;
import com.chai.yygh.model.hosp.Hospital;
import com.chai.yygh.model.hosp.HospitalSet;
import com.chai.yygh.model.hosp.Schedule;
import com.chai.yygh.service.DepartmentService;
import com.chai.yygh.service.HospitalService;
import com.chai.yygh.service.HospitalSetService;
import com.chai.yygh.service.ScheduleService;
import com.chai.yyph.common.helper.HttpRequestHelper;
import com.chai.yyph.common.utils.MD5;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 接受医院管理端的请求，返回对应数据
 * @program: yygh
 * @author:
 * @create: 2023-04-10 22:30
 **/
@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    /**
     * 上传医院信息
     *
     * @param request 请求
     * @return {@link Result}<{@link String}>
     */
    @PostMapping("/saveHospital")
    public Result<String> saveHospital(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);

        //相同的签名才可以进行保存数据库（发送的是加密的sign）
        String sign = (String) switchMap.get("sign");
        //根据医院的编码查询数据库中的签名
        String hoscode = (String) switchMap.get("hoscode");
        HospitalSet hospitalSet = hospitalSetService.getSignByHoscode(hoscode);
        String signKey = MD5.encrypt(hospitalSet.getSignKey());
        if (!sign.equals(signKey)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        hospitalService.save(switchMap);
        return Result.ok("保存成功!");
    }

    /**
     * 根据医院名称查询本地
     */
    @PostMapping("/hospital/show")
    public Result showHospital(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);
        String sign = (String) switchMap.get("sign");
        String hoscode = (String) switchMap.get("hoscode");
        HospitalSet hospitalSet = hospitalSetService.getSignByHoscode(hoscode);
        String signKey = MD5.encrypt(hospitalSet.getSignKey());
        if (!sign.equals(signKey)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        Hospital hospital =hospitalService.getHospital(hoscode);
        if (hospital!=null){
            return Result.ok(hospital);
        }else{
            return Result.fail("没有该数据");
        }

    }

    /**
     * 上传科室
     *
     * @param request 请求
     * @return {@link Result}
     */
    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);
        String sign = (String) switchMap.get("sign");
        String hoscode = (String) switchMap.get("hoscode");
        HospitalSet hospitalSet = hospitalSetService.getSignByHoscode(hoscode);
        String signKey = hospitalSet.getSignKey();
        signKey = MD5.encrypt(signKey);
        if (!sign.equals(signKey)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.save(switchMap);
        return Result.ok("添加成功");
    }

    /**
     * 查询科室接口
     *
     * @param request 请求
     * @return {@link Result}<{@link Page}<{@link Department}>>
     */
    @PostMapping("/department/list")
    public Result<Page<Department>> showDepartment(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) switchMap.get("hoscode");
        HospitalSet hospitalSet = hospitalSetService.getSignByHoscode(hoscode);
        String signKey = hospitalSet.getSignKey();

        //签名的校验：不是单纯的md5加密
        if(!HttpRequestHelper.isSignEquals(switchMap, signKey)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }

        Page<Department> page = departmentService.getDepartmentByCode(switchMap);
        return Result.ok(page);
    }
    /**
     * 删除科室接口
     * */
    @PostMapping("/department/remove")
    public Result remove(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);
        String hoscode = (String) switchMap.get("hoscode");
        String depcode = (String) switchMap.get("depcode");
        HospitalSet hospitalSet = hospitalSetService.getSignByHoscode(hoscode);
        String signKey = hospitalSet.getSignKey();
        if(!HttpRequestHelper.isSignEquals(switchMap, signKey)) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        departmentService.removeByCode(hoscode,depcode);
        return Result.ok();
    }

    /**
     * 上传排班接口
     * */
    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);
        String sign = (String) switchMap.get("sign");
        String hoscode = (String) switchMap.get("hoscode");
        HospitalSet hospitalSet = hospitalSetService.getSignByHoscode(hoscode);
        String signKey = hospitalSet.getSignKey();
        signKey = MD5.encrypt(signKey);
        if (!sign.equals(signKey)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.save(switchMap);
        return Result.ok();
    }
    /**
     * 查询排班
     * */
    @PostMapping("/schedule/list")
    public Result list(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);
        String sign = (String) switchMap.get("sign");
        String hoscode = (String) switchMap.get("hoscode");
        HospitalSet hospitalSet = hospitalSetService.getSignByHoscode(hoscode);
        String signKey = hospitalSet.getSignKey();
        signKey = MD5.encrypt(signKey);
        if (!sign.equals(signKey)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        Page<Schedule> schedulePage=scheduleService.page(switchMap);
        return Result.ok(schedulePage);
    }
    /**
     * 删除排班接口
     * */
    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(parameterMap);
        String sign = (String) switchMap.get("sign");
        String hoscode = (String) switchMap.get("hoscode");
        String hosScheduleId = (String) switchMap.get("hosScheduleId");
        HospitalSet hospitalSet = hospitalSetService.getSignByHoscode(hoscode);
        String signKey = hospitalSet.getSignKey();
        signKey = MD5.encrypt(signKey);
        if (!sign.equals(signKey)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }
}
