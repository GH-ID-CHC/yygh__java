package com.chai.yygh.repository;

import com.chai.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    Schedule getSchduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
