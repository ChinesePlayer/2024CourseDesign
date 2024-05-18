package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    //根据活动名称、负责人名称和活动状态查询活动
    @Query("select a from Activity a where (?2='' or a.director.personName like %?2%) and (?1='' or a.activityName like %?1%) and (?3=null or a.status=?3)")
    List<Activity> findByDirectorNameNameStatus(String activityName, String directorName, Integer status);

    //根据personId和活动状态来查询某个人建立的活动
    @Query("select a from Activity a where a.director.personId=?1 and (?2=null or a.status=?2)")
    List<Activity> findByPersonIdAndStatus(Integer personId, Integer status);

    //根据学生id来查询该学生参加的所有活动
    @Query("select a from Activity a join a.students s where s.studentId=?1 ")
    List<Activity> findByStudentId(Integer studentId);
}
