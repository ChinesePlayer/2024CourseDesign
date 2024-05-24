package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    //通过作业ID和学生ID来查找答案
    @Query("select a from Answer a where a.homework.homeworkId=?1 and a.student.studentId=?2")
    Optional<Answer> findByHomeworkStudent(Integer homeworkId, Integer studentId);
}
