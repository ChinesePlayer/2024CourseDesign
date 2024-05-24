package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.EHonorType;
import org.fatmansoft.teach.models.Honor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HonorRepository extends JpaRepository<Honor, Integer> {
    @Query("select h from Honor h where h.student.studentId=?1")
    List<Honor> findByStudentId(Integer studentId);

    @Query("select h from Honor h where (?1=null or ?1='' or h.student.person.personName like %?1% or h.student.person.personNum like %?1% ) and (h.honorType.type=?2 or ?2=null) ")
    List<Honor> findByNumNameAndType(String numName, EHonorType type, Pageable pageable);

    @Query("select count(h) from Honor h where (?1=null or ?1='' or h.student.person.personName like %?1% or h.student.person.personNum like %?1% ) and (h.honorType.type=?2 or ?2=null) ")
    int countByNumNameAndType(String numName, EHonorType type);
}
