package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    @Query(value = "select max(activityId) from Activity  ")
    Integer getMaxId();
    Optional<Activity> findByActivityId(Integer activityId);
    @Query(value = "from Activity a where ?1='' or a.person.num= ?1 ")
    List<Activity> findActivityListByPersonNum(String personNum);
    @Query(value = "from Activity a where ?1='' or a.person.num like %?1% or a.person.name like %?1%")
    List<Activity> findFuzzyActivityListByPersonNumAndName(String personNumAndName);
    @Query(value = "from Activity s where ?1='' or s.state = ?1 ")
    List<Activity> findActivityListByState(String state);
    Optional<Activity> findByName(String name);
    @Query(value = "from Activity a where ?1=null or a.activityId = ?1 ")
    List<Activity> findActivityListByActivityId(Integer activityId);
    //@Query(value = "from Activity a where a.activityId =?1")
    //List findPersonnelListByActivityId(Integer activityId);
    @Query(value = "from Activity a where a.person.personId = ?1 ")
    List<Activity> findActivityListByHostPersonId(Integer personId);
}
