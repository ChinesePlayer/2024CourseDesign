package com.teach.javafx.controller.studentActivity;

import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXTextField;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;


public class StudentActivityEditorController {
    @FXML
    public MFXTextField activityNameField;
    @FXML
    public DatePicker start;
    @FXML
    public DatePicker end;
    @FXML
    public MFXTextField maxNumberField;

    private StudentActivityController opener;

    @FXML
    public void initialize(){
        //限制最大人数输入框只能输入整数
        CommonMethod.limitToNumber(maxNumberField);
    }

    public void init(StudentActivityController o){
        this.opener = o;
    }

    public void onSubmit(){
        String activityName = activityNameField.getText();
        String maxNumber = maxNumberField.getText();
        LocalDate start = this.start.getValue();
        LocalDate end = this.end.getValue();
        //对数据的有效性进行检查
        if(activityName == null || activityName.equals("")){
            MessageDialog.showDialog("请输入活动名称");
            return;
        }
        if(maxNumber == null || maxNumber.equals("")){
            MessageDialog.showDialog("请输入最大人数");
            return;
        }
        if(start == null){
            MessageDialog.showDialog("请选择开始时间");
            return;
        }
        if(end == null){
            MessageDialog.showDialog("请选择结束时间");
            return;
        }
        //结束时间不能早于开始时间
        if(end.isBefore(start)){
            MessageDialog.showDialog("结束时间不能早于开始时间");
            return;
        }
        //开始时间不能早于现在时间
        if(start.isBefore(LocalDate.now())){
            MessageDialog.showDialog("开始时间不能早于现在时间");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("activityId", null);
        req.add("activityName", activityName);
        //直接将当前申请人的ID传到后端去
        req.add("directorId", AppStore.getJwt().getId());
        req.add("start", CommonMethod.getDateString(start, CommonMethod.DATE_FORMAT));
        req.add("end", CommonMethod.getDateString(end, CommonMethod.DATE_FORMAT));
        req.add("maxNumber", maxNumber);
        //审核状态直接设为审核中
        req.add("status", 0);

        DataResponse res = HttpRequestUtil.request("/api/activity/saveActivity", req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("提交成功, 请耐心等待审核");
            //刷新数据
            opener.hasSaved();
            //关闭当前页面
            ((Stage)activityNameField.getScene().getWindow()).close();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
