package com.chai.yygh.repository;

import com.chai.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: yygh
 * @author:
 * @create: 2023-04-10 22:25
 **/
@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {

    Hospital getHospitalByHoscode(String hoscode);

    List<Hospital> findHospitalByHosnameLike(String hosname);
}
