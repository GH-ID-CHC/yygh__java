package com.chai.yygh.repository;

import com.chai.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartmentRepository extends MongoRepository<Department,String> {

    /**
     * 获取科室
     *
     * @param hoscode hoscode
     * @param depcode depcode
     * @return {@link Department}
     */
    Department getDepartmentByHoscodeAndDepcode(String hoscode, String depcode);
}
