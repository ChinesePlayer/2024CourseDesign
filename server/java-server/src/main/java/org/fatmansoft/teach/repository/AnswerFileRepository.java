package org.fatmansoft.teach.repository;

import org.fatmansoft.teach.models.AnswerFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerFileRepository extends JpaRepository<AnswerFile, Integer> {
    //获取指定答案的所有文件
    @Query("select af from AnswerFile af where af.answer.answerId=?1")
    List<AnswerFile> findByAnswerId(Integer answerId);
}
