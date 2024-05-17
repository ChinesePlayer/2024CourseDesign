package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.PersonActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PersonActivityRepository extends JpaRepository<PersonActivity,Integer> {
    @Query(value = "select max(personActivityId) from PersonActivity  ")
    Integer getMaxId();
    List<PersonActivity> findByActivityActivityId(Integer activityId);
    List<PersonActivity> findByPersonPersonId(Integer personId);//用personId找对应的活动
    @Query(value = "from PersonActivity s where ?1='' or s.state = ?1 ")
    List<PersonActivity> findPersonActivityListByState(String state);
    @Query(value = "from PersonActivity sc where sc.person.personId = ?1 and sc.activity.activityId = ?2")
    Optional<PersonActivity> findByPersonIdAndActivityId(Integer personId, Integer activityId);
    //@Query(value = "select  p from StudentActivity p, ActivityTime t where p.activity.activityId=t.activity.activityId and p.student.studentId = ?1 and t.activityDay=?2")
    //List<StudentActivity>findByStudentStudentIdAndActivityDay(Integer studentId, Integer activityDay);
    @Query(value = "from PersonActivity sa where ?1= null or sa.activity.activityId = ?1")
    List<PersonActivity> findPersonActivityListByActivityId(Integer activityId);
}