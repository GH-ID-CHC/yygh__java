package com.chai.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chai.yygh.DictFeignClient;
import com.chai.yygh.model.hosp.Hospital;
import com.chai.yygh.repository.HospitalRepository;
import com.chai.yygh.service.HospitalService;
import com.chai.yygh.vo.hosp.HospitalQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @program: yygh
 * @author:
 * @create: 2023-04-10 22:28
 **/
@Service
@Slf4j
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void save(Map<String, Object> switchMap) {
        //将map转换为json字符串再转对象类型
        String jsonString = JSONObject.toJSONString(switchMap);
        Hospital hospital = JSONObject.parseObject(jsonString, Hospital.class);

        //解决base64再转换过程中将+号转为空格的问题
        String logoData = (String) switchMap.get("logoData");
        if (!StringUtils.isEmpty(logoData)) {
            String reLogoData = logoData.replace(" ", "+");
            hospital.setLogoData(reLogoData);
        }

        //判断mongodb中是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital findHospital = hospitalRepository.getHospitalByHoscode(hoscode);
        //保存数据或者修改数据
        if (findHospital != null) {
            hospital.setStatus(findHospital.getStatus());
            hospital.setCreateTime(findHospital.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        } else {
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital getHospital(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> page(Integer page, Integer limit, HospitalQueryVo hospitalQueryVo) {

        ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING).withIgnoreCase(true);
        Hospital hospital = new Hospital();
        //复制属性给hosiptal
        if (hospitalQueryVo!=null){
            BeanUtils.copyProperties(hospitalQueryVo,hospital);
        }
        Example<Hospital> hospitalExample = Example.of(hospital,matcher);
        PageRequest pageRequest = PageRequest.of(page - 1, limit);
        Page<Hospital> all = hospitalRepository.findAll(hospitalExample, pageRequest);
        //信息中只有医院等级编号，省市编号，需要到进行处理赋值
        all.getContent().stream().forEach(hosp -> {
//            hosp为list集合中遍历的Hospital类
            this.setHospitalHosType(hosp);
        });
        return all;
    }
    /**
     * 修改上线状态
     * */
    @Override
    public void updateStatus(String id, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1 && id!=null && !id.isEmpty()) {
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    /**
     * 获取医院信息
     *
     * @param id id
     * @return {@code Hospital}
     */
    @Override
    public Hospital getInfo(String id) {
        if (id!=null){
            Hospital hospital = hospitalRepository.findById(id).get();
            //为hospital中param进行赋值
            return setHospitalHosType(hospital);
        }
        log.error("查询id信息，id为null");
        return null;
    }

    @Override
    public String getHospName(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        if(null != hospital) {
            return hospital.getHosname();
        }
        return "";
    }

    /**
     * 场景：根据医院的信息查询医院的等级
     * 重点：跨模块查询，使用feign。将调用service_cmn接口中提供的方法
     * 此处使用feign则调用service-client中的接口
     * */
    private Hospital setHospitalHosType(Hospital hosp) {
        //查询医院等级
        String hostypeString = dictFeignClient.getName("Hostype", hosp.getHostype());
        //查询省、市、地区
        String provinceString = dictFeignClient.getName(hosp.getProvinceCode());
        String cityString = dictFeignClient.getName(hosp.getCityCode());
        String districtString = dictFeignClient.getName(hosp.getDistrictCode());
        hosp.getParam().put("hostypeString",hostypeString);
        hosp.getParam().put("allString",provinceString+cityString+districtString);
        //需要进行返回，才能将数据放在
        return hosp;
    }

}
