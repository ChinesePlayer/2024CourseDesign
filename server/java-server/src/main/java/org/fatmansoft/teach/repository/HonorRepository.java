package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Honor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorRepository extends JpaRepository<Honor, Integer> {
    @Query("select h from Honor h where h.student.studentId=?1")
    List<Honor> findByStudentId(Integer studentId);
}
