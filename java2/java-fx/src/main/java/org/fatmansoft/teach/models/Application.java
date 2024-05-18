package org.fatmansoft.teach.models;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

//请假类
public class Application {

    private static Map<String, String> typesMap = new HashMap<>();

    static {
        typesMap.put("暂时离校", "APP_TEMP");
        typesMap.put("长期离校", "APP_LONG");
        typesMap.put("永久离校", "APP_FOREVER");
    }

    private Integer applicationId;

    private Integer studentId;

    private String applicationType;

    //申请理由
    private String reason;

    //离校时间
    private LocalDate leaveDate;
    //返校时间
    private LocalDate returnDate;

    //假条状态：0: 审批中  1: 已通过  2: 未通过  3: 已销假
    private Integer status;

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(LocalDate leaveDate) {
        this.leaveDate = leaveDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    //通过请假类型的字符串来获取展示给用户的字符串
    public static String getDisplayString(String appType){
        final String[] res = new String[1];
        typesMap.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String string, String string2) {
                if(appType.equals(string2)){
                    res[0] = string;
                }
            }
        });
        return res[0];
    }

    //通过展示给用户的字符串获取请假类型字符串
    public static String getTypeString(String displayStr){
        return typesMap.get(displayStr);
    }
}
