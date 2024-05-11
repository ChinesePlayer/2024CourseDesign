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
@Table(name = "homework_file")
public class HomeworkFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;

    @ManyToOne
    @JoinColumn(name = "homework_id")
    private Homework homework;

    @JoinColumn(name = "file_path")
    private String filePath;

    @JoinColumn(name = "file_name")
    private String fileName;

    public byte[] getByteArray() throws IOException{
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
