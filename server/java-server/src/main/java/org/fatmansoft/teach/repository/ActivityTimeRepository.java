package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.ActivityTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Course 数据操作接口，主要实现Activity数据的查询操作
 */
public interface ActivityTimeRepository extends JpaRepository<ActivityTime, Integer> {
    @Query(value = "select max(activityTimeId) from ActivityTime  ")
    Integer getMaxId();
    List<ActivityTime> findByActivityActivityId(Integer activityId);
}