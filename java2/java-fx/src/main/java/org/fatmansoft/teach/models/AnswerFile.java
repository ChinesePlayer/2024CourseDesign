package org.fatmansoft.teach.models;

import org.fatmansoft.teach.util.CommonMethod;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnswerFile {
    private Integer fileId;
    //文件的本地路径
    private String filePath;
    private Long fileSize;
    private String fileName;

    public AnswerFile(){

    }

    public AnswerFile(Map m){
        this.fileId = CommonMethod.getInteger(m, "fileId");
        this.fileSize = CommonMethod.getLong(m, "fileSize");
        this.fileName = CommonMethod.getString(m, "fileName");
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static List<Path> buildFilePaths(List<AnswerFile> files){
        if (files == null){
            return new ArrayList<>();
        }
        List<Path> pathList = new ArrayList<>();
        for(AnswerFile file : files){
            Path p = Paths.get(file.getFilePath());
            pathList.add(p);
        }
        return pathList;
    }
}
