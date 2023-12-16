package com.chai.cmn.controller;

import com.chai.cmn.service.DictService;
import com.chai.yygh.common.result.Result;
import com.chai.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @program: yygh
 * @author:
 * @create: 2023-02-19 16:32
 **/
@Api(value = "数据字典的接口")
@RestController
@RequestMapping("/admin/cmn/dict")
@CrossOrigin
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation("将文件中的数据导入到数据库中")
    @PostMapping("/importData")
    public Result importData(MultipartFile file) {
        dictService.importDictData(file);
        return Result.ok();
    }

    @ApiOperation("导出数据到Excel表中")
    @GetMapping(value = "/exportData")
    public void exportData(HttpServletResponse response) {
        dictService.export(response);
        //不需要有返回值数据
    }

    @ApiOperation("根据id查询数据下面的子数据列表")
    @GetMapping("/findChildData/{id}")
    public Result<List> findChildData(@PathVariable Long id) {
        List<Dict> dicts = dictService.findChildData(id);
        return Result.ok(dicts);
    }

    /**
     * 根据上级编码与值获取数据字典名称
     */
    @ApiOperation("获取数据字典的名称")
    @GetMapping("/getName/{parentDictCode}/{value}")
    public String getName(
            @PathVariable String parentDictCode,
            @PathVariable String value) {
        String name = dictService.getName(parentDictCode, value);
        return name;
    }

    /**
     * 根据value获取数据字典的值
     */
    @ApiOperation("获取数据字典的名称")
    @GetMapping("/getName/{value}")
    public String getName(
            @PathVariable String value) {
        String name = dictService.getName("", value);
        return name;
    }

    /**
     * 获取所有下级子节点
     * 根据dict_code返回下面的子列表
     */
    @ApiOperation("获取子节点的列表")
    @GetMapping("/findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(@PathVariable(value = "dictCode") String dictCode) {
        List<Dict> dictList=dictService.findByDictCode(dictCode);
        return Result.ok(dictList);
    }
}

