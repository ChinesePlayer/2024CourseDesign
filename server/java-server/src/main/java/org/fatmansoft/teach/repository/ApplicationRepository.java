package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    //根据学生id和假条状态查询
    @Query("select a from Application a where (?1=null or a.student.studentId=?1) and (?2=null or a.status=?2)")
    List<Application> findByStudentIdAndStatus(Integer studentId, Integer status);
}
