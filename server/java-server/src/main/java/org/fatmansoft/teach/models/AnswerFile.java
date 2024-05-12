package org.fatmansoft.teach.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Entity
@Getter
@Setter
@Table(name = "answer_file")
public class AnswerFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;

    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;

    @JoinColumn(name = "file_path")
    private String filePath;

    @JoinColumn(name = "file_name")
    private String fileName;

    public byte[] getByteArray() throws IOException {
        File file = new File(filePath);
        return Files.readAllBytes(file.toPath());
    }

    public Long getFileSize(){
        File file = new File(filePath);
        if(file.exists()){
            return file.length();
        }
        else {
            return null;
        }
    }
}
