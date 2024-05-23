package com.teach.javafx.controller.studentAffair;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Application;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ApplicationEditorController {
    @FXML
    public TextArea reasonField;
    @FXML
    public DatePicker leaveDate;
    @FXML
    public DatePicker returnDate;
    @FXML
    public ComboBox<String> typeCombo;

    private Application application;
    private StudentApplicationController opener;

    //请假类型和值的映射
    private Map<String, String> typeMap = new HashMap<>();

    {
        typeMap.put("暂时离校","APP_TEMP");
        typeMap.put("长期离校","APP_LONG");
        typeMap.put("永久离校","APP_FOREVER");
    }

    @FXML
    public void initialize(){
        typeCombo.setItems(FXCollections.observableArrayList("暂时离校","长期离校","永久离校"));
        typeCombo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String selected = typeCombo.getSelectionModel().getSelectedItem();
                //当用户选择永久离校时，禁用返校日期的选择框
                if(selected.equals("永久离校")){
                    returnDate.setDisable(true);
                }
                else{
                    returnDate.setDisable(false);
                }
            }
        });
    }

    public void init(Application a, StudentApplicationController asCont){
        this.opener = asCont;
        this.application = a;
        setDataView(a);
    }

    //请假类型和值的逆映射
    private String reverseMap(String value){
        final String[] res = new String[1];
        typeMap.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String string, String string2) {
                if(string2.equals(value)){
                    res[0] = string;
                }
            }
        });
        return res[0];
    }

    private void setDataView(Application a){
        if(a == null){
            return;
        }
        reasonField.setText(a.getReason());
        typeCombo.getSelectionModel().select(reverseMap(a.getApplicationType()));
        leaveDate.setValue(a.getLeaveDate());
        returnDate.setValue(a.getReturnDate());
        if(typeCombo.getSelectionModel().getSelectedItem().equals("永久离校")){
            returnDate.setDisable(true);
        }
    }

    public void onSave(){
        //验证数据有效性
        String reason = reasonField.getText();
        String type = typeMap.get(typeCombo.getSelectionModel().getSelectedItem());
        String leaveDate = CommonMethod.getDateString(this.leaveDate.getValue(),CommonMethod.DATE_FORMAT);
        String returnDate;

        //检查返校日期的选择框是否被禁用，若被禁用，则说明选择了永久离校
        if(this.returnDate.isDisable()){
            returnDate = null;
        }
        else{
            returnDate = CommonMethod.getDateString(this.returnDate.getValue(),CommonMethod.DATE_FORMAT);
        }
        if(reason == null || reason.isEmpty()){
            MessageDialog.showDialog("请输入请假理由!");
            return;
        }
        if(type == null){
            MessageDialog.showDialog("请选择请假类别!");
            return;
        }
        if(leaveDate == null){
            MessageDialog.showDialog("请选择离校日期!");
            return;
        }
        //不允许离校时间早于今天
        if(this.leaveDate.getValue().isBefore(LocalDate.now())){
            MessageDialog.showDialog("离校日期不能早于今天!");
            return;
        }
        if(!type.equals("APP_FOREVER") && returnDate == null){
            MessageDialog.showDialog("请选择返校日期!");
            return;
        }
        //不允许返校日期早于离校日期
        if(!type.equals("APP_FOREVER") && this.returnDate.getValue().isBefore(this.leaveDate.getValue())){
            MessageDialog.showDialog("返校日期不能早于离校日期!");
            return;
        }
        //检查天数是否足够长期离校
        //天数大于等于30天才能选择长期离校
        if(type.equals("APP_LONG")){
            long dayDiff = ChronoUnit.DAYS.between(this.leaveDate.getValue(), this.returnDate.getValue());
            if(dayDiff < 30){
                MessageDialog.showDialog("天数不足30天, 请选择暂时离校!");
                return;
            }
        }

        DataRequest req = new DataRequest();
        req.add("applicationId",application == null ? null : application.getApplicationId());
        req.add("applicationType",type);
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("leaveDate",leaveDate);
        req.add("returnDate",returnDate);
        req.add("reason",reason);

        DataResponse res = HttpRequestUtil.request("/api/application/saveApplication",req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("提交成功!");
            //关闭当前窗口
            ((Stage)reasonField.getScene().getWindow()).close();
            //刷新数据
            opener.hasSaved();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
