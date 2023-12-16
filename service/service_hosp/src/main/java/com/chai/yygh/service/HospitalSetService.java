package com.chai.yygh.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chai.yygh.model.hosp.HospitalSet;

public interface HospitalSetService extends IService<HospitalSet> {
    HospitalSet getSignByHoscode(String hoscode);
}
