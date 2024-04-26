package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.CourseTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseTimeRepository extends JpaRepository<CourseTime, Integer> {
    //通过课程ID来查找该课程的上课时间
    @Query("select ct from CourseTime ct where ct.course.courseId=?1")
    List<CourseTime> findCourseTimeByCourseId(Integer courseId);

    //查询最大ID
    @Query("select max(ct.courseTimeId) from CourseTime ct")
    Integer findMaxId();

}
