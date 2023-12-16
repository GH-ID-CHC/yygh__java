package com.chai.yygh.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chai.yygh.common.result.Result;
import com.chai.yygh.model.hosp.HospitalSet;
import com.chai.yygh.service.HospitalSetService;
import com.chai.yygh.vo.hosp.HospitalSetQueryVo;
import com.chai.yyph.common.utils.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * @program: yygh
 * @author:
 * @create: 2023-01-15 17:19
 **/
@Api(tags = "医院设置管理")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation(value = "获取所有的医院数据")
    @GetMapping("/findAll")
    public Result<List> findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return Result.ok(list);
    }

    @ApiOperation(value = "根据id逻辑删除")
    @DeleteMapping("{id}")
    public Result<Boolean> removeHospSet(@PathVariable Long id){
        boolean b = hospitalSetService.removeById(id);
        if (b){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    /**
     * requestBody以json形式用对象接受
     * required=false可以为空
     * @requesBody需要用Post请求才能获取
     * @param current 当前页
     * @param limit  显示数据
     * @param hospitalSetQueryVo 封装的条件
     * */
    @ApiOperation(value = "条件查询并分页")
    @PostMapping("/findPageHospSet/{current}/{limit}")
    public Result<Page> findPageHospSet(@PathVariable Long current,
                                  @PathVariable Long limit,
                                  @RequestBody(required = false) HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> page = new Page<>(current,limit);
        LambdaQueryWrapper<HospitalSet> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(!StringUtils.isEmpty(hospitalSetQueryVo.getHosname()),HospitalSet::getHosname,hospitalSetQueryVo.getHosname());
        wrapper.eq(!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()),HospitalSet::getHoscode,hospitalSetQueryVo.getHoscode());
        Page<HospitalSet> hospitalSetPage = hospitalSetService.page(page, wrapper);
        return Result.ok(hospitalSetPage);
    }

    /**
     * 添加医院的设置
     *
     * */
    @PostMapping("/saveHospitalSet")
    public Result saveHospitalSet(@RequestBody HospitalSet hospitalSet){
        //设置状态  1 使用 ;  0 不能
        hospitalSet.setStatus(1);
        //签名密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));

        boolean save = hospitalSetService.save(hospitalSet);
        if (save){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    /**
     * 根据id获取医院设置
     * */
    @GetMapping("/getHospSet/{id}")
    public Result getHospSet(@PathVariable Long id){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        if (hospitalSet!=null){
            return Result.ok(hospitalSet);
        }else{
            return Result.fail("查询失败");
        }
    }

    /**
     * 根据id修改医院设置
     * */
    @PutMapping("/updateHospSet")
    public Result updateHospSet(@RequestBody HospitalSet hospitalSet){
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b){
            return Result.ok();
        }else{
            return Result.fail("修改失败");
        }
    }

    /**
     * 根据id批量删除
     * */
    @DeleteMapping("/batchRemove")
    public Result batchRemoveHospSet(@RequestBody List<Long> ids){
        boolean b = hospitalSetService.removeByIds(ids);
        if (b){
            return Result.ok();
        }else{
            return Result.fail("修改失败");
        }

    }

    /**
     * 医院设置锁定和解锁
     * @param id 医院id
     * @param status 状态
     * */
    @PutMapping("/lockHospitalSet/{id}/{status}")
    public Result lockHospitalSet(@PathVariable Long id,
                                  @PathVariable Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        boolean b = hospitalSetService.updateById(hospitalSet);
        if (b){
            return Result.ok("成功");
        }else{
            return Result.fail("修改失败");
        }
    }

    /**
     * 发送签名密钥
     * */
    @PutMapping("sendKey/{id}")
    public Result lockHospitalSet(@PathVariable Long id) {
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        String signKey = hospitalSet.getSignKey();
        String hoscode = hospitalSet.getHoscode();
        return Result.ok();
    }
}
