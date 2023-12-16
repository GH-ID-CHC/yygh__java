package com.chai.yygh.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.chai.yygh.model.hosp.Department;
import com.chai.yygh.repository.DepartmentRepository;
import com.chai.yygh.service.DepartmentService;
import com.chai.yygh.vo.hosp.DepartmentVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: yygh
 * @author:
 * @create: 2023-04-15 22:29
 **/
@Service
@Transactional
@Slf4j
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;


    @Override
    public Page<Department> getDepartmentByCode(Map<String, Object> switchMap) {
        String jsonString = JSONObject.toJSONString(switchMap);
        Department department = JSONObject.parseObject(jsonString, Department.class);
        Integer pageNum = Integer.parseInt((String) switchMap.get("page"));
        Integer pageSize = Integer.parseInt((String) switchMap.get("limit"));
        if (StringUtils.isEmpty(pageNum)) {
            //设置初始值
            pageNum = 1;
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = 10;
        }
        //设置查询条件
        Department isDepartment = new Department();
        isDepartment.setHoscode(department.getHoscode());
        isDepartment.setIsDeleted(0);
        //分页查询
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        //查询的规则
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase(true);

        //封装查询条件和查询的规则
        Example<Department> departmentExample = Example.of(isDepartment, matcher);
        Page<Department> all = departmentRepository.findAll(departmentExample, pageable);
        return all;
    }

    @Override
    public void save(Map<String, Object> switchMap) {
        String jsonString = JSONObject.toJSONString(switchMap);
        Department department = JSONObject.parseObject(jsonString, Department.class);
        String hoscode = department.getHoscode();
        String depcode = department.getDepcode();
        //当医院id和科室的id同时相同时，执行修改操作
        Department mongoDepartment = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (mongoDepartment != null) {
            department.setCreateTime(mongoDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(mongoDepartment.getIsDeleted());
            department.setId(mongoDepartment.getId());
            departmentRepository.save(department);
        } else {
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }
    }

    @Override
    public void removeByCode(String hoscode, String depcode) {
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null) {
            departmentRepository.deleteById(department.getId());
        }

    }

    /**
     * 获取医院的科室列表
     *
     * @param hoscode 医院编号
     * @return {@code List<HospitalSet>}
     */
    @Override
    public List<DepartmentVo> getDepList(String hoscode) {
        ArrayList<DepartmentVo> departmentVos = new ArrayList<>();
        if (hoscode!=null){
            //科室的编号
            Department department = new Department();
            department.setHoscode(hoscode);
            Example<Department> departmentExample = Example.of(department);
            List<Department> departmentList = departmentRepository.findAll(departmentExample);
            //将list中的数据进行分组，分组的条件Bigcode
            Map<String, List<Department>> collect = departmentList.stream().collect(Collectors.groupingBy(Department::getBigname));
            for (String bigName : collect.keySet()) {
                DepartmentVo departmentVo = new DepartmentVo();
                //根据不同的科室查询
                List<Department> departments = collect.get(bigName);
                //科室名称
                departmentVo.setDepname(bigName);
                //科室编号
                departmentVo.setDepcode(departments.get(0).getBigcode());
                List<DepartmentVo> departmentVoList = new ArrayList<>();
                for (Department children : departments) {
                    DepartmentVo childrenVo = new DepartmentVo();
                    childrenVo.setDepcode(children.getDepcode());
                    childrenVo.setDepname(children.getDepname());
                    departmentVoList.add(childrenVo);
                }
                departmentVo.setChildren(departmentVoList);
                departmentVos.add(departmentVo);
            }
            //科室的名称
            //下级菜单
        }
        log.error("医院编号错误");
        return departmentVos;
    }
}
