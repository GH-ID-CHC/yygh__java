package com.chai.cmn.service.Impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chai.cmn.listener.DictListener;
import com.chai.cmn.mapper.DictMapper;
import com.chai.cmn.service.DictService;
import com.chai.yygh.model.cmn.Dict;
import com.chai.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: yygh
 * @author:
 * @create: 2023-02-19 16:34
 **/
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {
    @Autowired
    private DictMapper dictMapper;

    //根据id查询子节点的数据
    @Override
    public List<Dict> findChildData(Long id) {
        //select * from dict where parentId =id
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getParentId, id);
        List<Dict> dicts = dictMapper.selectList(wrapper);
        for (Dict dict : dicts) {
            //查看该子节点下面是否有字节点
            dict.setHasChildren(isChildren(dict.getId()));
        }
        return dicts;
    }

    //查询所有子节点
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        if (dictCode == null) {
            return null;
        }
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getDictCode, dictCode);
        Dict dict = dictMapper.selectOne(wrapper);
        LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dict::getParentId, dict.getId());
        List<Dict> dictList = dictMapper.selectList(queryWrapper);
        return dictList;
    }

    //导出数据到表格中
    @Override
    public void export(HttpServletResponse response) {
        try {
            /**设置下载的信息*/
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("数据字典", "UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            /**查询数据库*/
            List<Dict> dictList = dictMapper.selectList(null);
            //   Dict      --转换-->   DictEeVo
            List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
            for (Dict dict : dictList) {
                DictEeVo dictVo = new DictEeVo();
                BeanUtils.copyProperties(dict, dictVo, DictEeVo.class);
                dictVoList.add(dictVo);
            }
            //通过流的方式写入到文件中
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("数据字典").doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(), new DictListener(dictMapper)).sheet().doRead();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //根据上级编码与值获取数据字典名称
    @Override
    public String getName(String parentDictCode, String value) {
        if (!StringUtils.isEmpty(parentDictCode)) {
            //根据两个参数进行查询
            LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Dict::getDictCode, parentDictCode);
            Dict dict = dictMapper.selectOne(wrapper);
            //查询条件是将上面查出的id作为parentId
            LambdaQueryWrapper<Dict> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Dict::getParentId, dict.getId());
            queryWrapper.eq(Dict::getValue, value);
            Dict queryDict = dictMapper.selectOne(queryWrapper);
            return queryDict.getName();
        } else {
            //这里指查询value唯一的，比如身份证，户口本。如果是查省会携带DictCode参数
            //根据value进行查询
            LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Dict::getValue, value);
            Dict dict = dictMapper.selectOne(wrapper);
            return dict.getName();
        }
    }

    //判断该节点下面是否有子节点
    public Boolean isChildren(Long id) {
        LambdaQueryWrapper<Dict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dict::getParentId, id);
        Integer count = dictMapper.selectCount(wrapper);
        return count > 0;
    }
}
