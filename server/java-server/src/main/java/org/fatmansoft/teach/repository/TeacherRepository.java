package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher,Integer> {
    @Query(value = "from Teacher where ?1='' or person.personNum like %?1% or person.personName like %?1% ")
    List<Teacher> findTeacherListByNumName(String numName);

    @Query(value="select t from Teacher t, User u where u.person.personId = t.person.personId and u.userId=?1")
    Optional<Teacher> findByUserId(Integer userId);

}
