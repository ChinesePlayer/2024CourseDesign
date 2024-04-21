package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseSelectionTurn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSelectionTurnRepository extends JpaRepository<CourseSelectionTurn, Integer> {
    //查询当前选课轮次的所有课程
    @Query("select t.courses from CourseSelectionTurn t where t.courseSelectionId=?1")
    List<Course> findCoursesByTurnId(Integer turnId);
}
