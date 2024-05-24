package com.teach.javafx.controller.studentActivity;

import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Activity;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ActivityEditorController {
    @FXML
    public MFXTextField activityNameField;
    @FXML
    public ComboBox<Person> directorCombo;
    @FXML
    public DatePicker start;
    @FXML
    public DatePicker end;
    @FXML
    public MFXTextField maxNumberField;
    @FXML
    public ComboBox<String> statusCombo;

    private Activity activity;
    private ActivityController opener;
    private Map<String, Integer> statusMap;
    private List<Person> personList = new ArrayList<>();

    @FXML
    public void initialize(){
        //限制最大人数输入框只能输入整数
        CommonMethod.limitToNumber(maxNumberField);
        getPersonList();
        directorCombo.setItems(FXCollections.observableArrayList(personList));
    }

    public void init(Activity a, ActivityController acont){
        this.activity = a;
        this.opener = acont;
        //获取审批状态映射Map
        statusMap = acont.getStatusMapper();
        statusCombo.setItems(FXCollections.observableArrayList("审批中", "已通过", "未通过"));
        //只有在传入的活动对象不是空时才设置视图信息
        if(a != null){
            setDataView(a);
        }
    }

    //从后端获取所有人员信息，以选择活动负责人
    public void getPersonList(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/activity/getPersonList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            //先清空
            personList.clear();
            for(Map m : rawData){
                Person p = new Person(m);
                personList.add(p);
            }
        }
    }

    //审批状态的逆向映射
    public String inverseMap(Integer status){
        final String[] res = new String[1];
        statusMap.forEach((string, integer) -> {
            if(Objects.equals(integer, status)){
                res[0] = string;
            }
        });
        return res[0];
    }

    public Person findPersonById(List<Person> personList, Integer personId){
        for(Person p : personList){
            if(Objects.equals(p.getPersonId(), personId)){
                return p;
            }
        }
        return null;
    }

    //根据传入的Activity来设置整个页面的显示信息
    public void setDataView(Activity a){
        this.activityNameField.setText(a.getActivityName());
        this.statusCombo.getSelectionModel().select(inverseMap(a.getStatus()));
        this.start.setValue(a.getStart());
        this.end.setValue(a.getEnd());
        this.maxNumberField.setText(a.getMaxNumber().toString());
        this.directorCombo.getSelectionModel().select(findPersonById(personList, a.getDirectorId()));
    }

    public void onSave(){
        String activityName = activityNameField.getText();
        String maxNumber = maxNumberField.getText();
        LocalDate start = this.start.getValue();
        LocalDate end = this.end.getValue();
        Person director = this.directorCombo.getSelectionModel().getSelectedItem();
        String status = this.statusCombo.getSelectionModel().getSelectedItem();
        //对数据的有效性进行检查
        if(activityName == null || activityName.equals("")){
            MessageDialog.showDialog("请输入活动名称");
            return;
        }
        if(maxNumber == null || maxNumber.equals("")){
            MessageDialog.showDialog("请输入最大人数");
            return;
        }
        if(status == null || status.equals("")){
            MessageDialog.showDialog("请选择审批状态");
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
        if(director == null || director.getPersonId() == null || director.getPersonId() <= 0){
            MessageDialog.showDialog("请选择负责人");
            return;
        }
        DataRequest req = new DataRequest();
        req.add("activityId", activity == null ? null : activity.getActivityId());
        req.add("activityName", activityName);
        req.add("directorId",director.getPersonId());
        req.add("start", CommonMethod.getDateString(start, CommonMethod.DATE_FORMAT));
        req.add("end", CommonMethod.getDateString(end, CommonMethod.DATE_FORMAT));
        req.add("maxNumber", maxNumber);
        req.add("status", statusMap.get(status));

        DataResponse res = HttpRequestUtil.request("/api/activity/saveActivity", req);
        assert res != null;
        if(res.getCode() == 0){
            MessageDialog.showDialog("保存成功");
            //刷新显示页面
            opener.hasSaved();
            //关闭当前页面
            ((Stage)activityNameField.getScene().getWindow()).close();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
