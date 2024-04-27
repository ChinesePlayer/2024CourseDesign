package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.CompletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompletionStatusRepository extends JpaRepository<CompletionStatus, Integer> {
    @Query("select cs from CompletionStatus cs where cs.student.studentId=?1")
    List<CompletionStatus> findCompletionStatusesByStudentId(Integer studentId);

    @Query("select cs from CompletionStatus cs where cs.student.studentId=?1 and cs.course.courseId=?2")
    Optional<CompletionStatus> findCompletionStatusByStudentIdAndCourseId(Integer studentId, Integer courseId);
}
