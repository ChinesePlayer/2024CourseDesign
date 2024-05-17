package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    //根据活动名称、负责人名称和活动状态查询活动
    @Query("select a from Activity a where (?2='' or a.director.name like %?2%) and (?1='' or a.activityName like %?1%) and (?3=null or a.status=?3)")
    List<Activity> findByDirectorNameNameStatus(String activityName, String directorName, Integer status);
}
