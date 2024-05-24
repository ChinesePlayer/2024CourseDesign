package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework, Integer> {
    @Query("select h from Homework h where h.course.courseId=?1")
    List<Homework> findByCourseId(Integer courseId);
}
