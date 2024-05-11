package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.HomeworkFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkFileRepository extends JpaRepository<HomeworkFile, Integer> {
    @Query("select h.files from Homework h where h.homeworkId=?1")
    List<HomeworkFile> findByHomeworkId(Integer homeworkId);
}
