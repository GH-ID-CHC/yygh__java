package com.chai.yygh.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chai.yygh.mapper.HospitalSetMapper;
import com.chai.yygh.model.hosp.HospitalSet;
import com.chai.yygh.service.HospitalSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: yygh
 * @author:
 * @create: 2023-01-15 17:11
 **/
@Service
public class HospitalSetServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {
    @Autowired
    private HospitalSetMapper hospitalSetMapper;
    @Override
    public HospitalSet getSignByHoscode(String hoscode) {
        LambdaQueryWrapper<HospitalSet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(hoscode!=null,HospitalSet::getHoscode,hoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(wrapper);
        return hospitalSet;
    }
}
