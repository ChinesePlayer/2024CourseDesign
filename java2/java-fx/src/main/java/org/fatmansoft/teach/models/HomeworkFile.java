package org.fatmansoft.teach.models;

import org.fatmansoft.teach.util.CommonMethod;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeworkFile {
    private String fileName;
    private Integer fileId;
    private Long fileSize;
    //前端本地的文件路径
    private String filePath;

    public HomeworkFile(){

    }

    public HomeworkFile(Map m){
        this.fileName = CommonMethod.getString(m, "fileName");
        this.fileId = CommonMethod.getInteger(m, "fileId");
        this.fileSize = CommonMethod.getLong(m, "fileSize");
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static List<Path> buildFilePaths(List<HomeworkFile> files){
        if (files == null){
            return new ArrayList<>();
        }
        List<Path> pathList = new ArrayList<>();
        for(HomeworkFile file : files){
            Path p = Paths.get(file.getFilePath());
            pathList.add(p);
        }
        return pathList;
    }
}
