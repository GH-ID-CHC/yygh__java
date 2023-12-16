package com.chai.yygh.controller;/**
 * Author: CHAI
 * Date: 2023/7/6
 */

import com.chai.yygh.common.result.Result;
import com.chai.yygh.service.DepartmentService;
import com.chai.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: yygh
 * @author:
 * @create: 2023-07-06 22:13
 **/
@Api(tags = "科室管理信息")
@RestController
@RequestMapping("/admin/hosp/department")
@CrossOrigin
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "获取科室列表")
    @GetMapping("/getDepList/{hoscode}")
    public Result<List<DepartmentVo>> getDepList(@PathVariable String hoscode) {
        List<DepartmentVo> list=departmentService.getDepList(hoscode);
        return Result.ok(list);
    }
}
