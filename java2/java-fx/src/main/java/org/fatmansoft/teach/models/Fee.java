package org.fatmansoft.teach.models;

import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Fee {
    private LocalDate dateTime;
    private Double value;

    public Fee(){

    }

    public Fee(Map m){
        //按照后端的格式解析日期信息
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        this.dateTime = LocalDate.parse(CommonMethod.getString(m, "title"), formatter);
        this.value = CommonMethod.getDouble(m, "value");
    }


    public LocalDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDate date) {
        this.dateTime = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
