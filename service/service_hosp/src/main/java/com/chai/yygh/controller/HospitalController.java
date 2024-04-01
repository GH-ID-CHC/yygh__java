package com.chai.yygh.controller;

import com.chai.yygh.common.result.Result;
import com.chai.yygh.model.hosp.Hospital;
import com.chai.yygh.service.HospitalService;
import com.chai.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @program: yygh
 * @author:
 * @create: 2023-04-17 21:09
 **/
@Api(tags = "医院管理信息")
@RestController
@RequestMapping("/admin/hosp/hospital")
@CrossOrigin
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    /**
     * 医院管理的所有医院的列表数据
     *
     * @param page            当前页
     * @param limit           记录数
     * @param hospitalQueryVo 医院查询vo
     * @return {@link Result}<{@link Page}<{@link Hospital}>>
     */
    @ApiOperation(value = "获取分页列表")
    @GetMapping("/list/{page}/{limit}")
    public Result<Page<Hospital>> list(
            @PathVariable Integer page,
            @PathVariable Integer limit,
            @ApiParam(name = "hospitalQueryVo", value = "查询对象", required = false)
            HospitalQueryVo hospitalQueryVo) {
        if (page == null) {
            page = 1;
        }
        if (limit == null) {
            limit = 10;
        }
        Page<Hospital> hospitalPage = hospitalService.page(page, limit, hospitalQueryVo);
        return Result.ok(hospitalPage);
    }

    /**
     * 修改医院的状态
     * */
    @ApiOperation(value = "更新上线状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return Result.ok();
    }

    /**
     * 获取医院的信息
     * */
    @GetMapping("/getInfo/{id}")
    public Result getInfo(@PathVariable String id){
        Hospital hospital=hospitalService.getInfo(id);
        return Result.ok(hospital);
    }
}
