package org.fatmansoft.teach.models;

import javafx.scene.control.Button;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.util.*;
import java.util.function.BiConsumer;

//请假类
public class Application {

    //请假类型映射
    //类型名称 ---- 类型值
    private static Map<String, String> typesMap = new HashMap<>();
    //状态码----状态名称
    private static Map<String,Integer> statusMap = new HashMap<>();

    static {
        typesMap.put("暂时离校", "APP_TEMP");
        typesMap.put("长期离校", "APP_LONG");
        typesMap.put("永久离校", "APP_FOREVER");

        statusMap.put("全部",null);
        statusMap.put("审批中",0);
        statusMap.put("已通过",1);
        statusMap.put("未通过",2);
        statusMap.put("已销假",3);
    }

    private Integer applicationId;

    private Integer studentId;
    private String studentName;

    private String applicationType;

    //申请理由
    private String reason;

    //离校时间
    private LocalDate leaveDate;
    //返校时间
    private LocalDate returnDate;

    //假条状态：0: 审批中  1: 已通过  2: 未通过  3: 已销假
    private Integer status;

    private List<Button> actions = new ArrayList<>();

    public Application(){

    }

    public Application(Map m){
        this.applicationId = CommonMethod.getInteger(m,"applicationId");
        this.studentId = CommonMethod.getInteger(m,"studentId");
        this.reason = CommonMethod.getString(m, "reason");
        this.status = CommonMethod.getInteger(m,"status");
        this.leaveDate = CommonMethod.getLocalDateFromString(CommonMethod.getString(m,"leaveDate"),CommonMethod.DATE_FORMAT);
        this.returnDate = CommonMethod.getLocalDateFromString(CommonMethod.getString(m,"returnDate"),CommonMethod.DATE_FORMAT);
        this.studentName = CommonMethod.getString(m,"studentName");
        this.applicationType = CommonMethod.getString(m,"applicationType");
    }

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

    public static String getStatusName(Integer code){
        final String[] res = new String[1];
        statusMap.forEach((string, integer) -> {
            if(Objects.equals(integer, code)){
                res[0] = string;
            }
        });
        return res[0];
    }

    public static Integer getStatusCode(String statusName){
        return statusMap.get(statusName);
    }

    //通过展示给用户的字符串获取请假类型字符串
    public static String getTypeString(String displayStr){
        return typesMap.get(displayStr);
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<Button> getActions() {
        return actions;
    }

    public void setActions(List<Button> actions) {
        this.actions = actions;
    }
}
