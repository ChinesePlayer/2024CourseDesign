package com.teach.javafx.controller.studentActivity;

import com.teach.javafx.controller.base.LocalDateStringConverter;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import com.teach.javafx.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController {
    @FXML
    public TableColumn HDSPColumn;
    @FXML
    public TableColumn leftPeopleRemainingColumn;
    @FXML
    public Label personNameLabel;
    @FXML
    public Label leftPeopleRemainingLabel;
    public Integer show = 1;
    @FXML
    public Button showButton;
    @FXML
    public Button passButton;
    @FXML
    public Button remakeButton;
    @FXML
    public Button saveButton;
    @FXML
    public DatePicker timestartPicker;  //教师信息  出生日期选择域
    @FXML
    public DatePicker timeendPicker;  //教师信息  出生日期选择域
    @FXML
    public TableView<Map> dataTableView;
    @FXML
    public TableColumn<Map,String> nameColumn;
    @FXML
    public TableColumn<Map,String> addressColumn;
    @FXML
    public TableColumn<Map,String> personNameColumn;
    @FXML
    public TableColumn<Map,String> timestartColumn; //教师信息表 出生日期列
    @FXML
    public TableColumn<Map,String> timeendColumn; //教师信息表 出生日期列
    @FXML
    public TableColumn<Map,String> volunteertimeColumn;
    @FXML
    public TableColumn<Map,String> studentColumn;
    @FXML
    public TableColumn<Map,String> joinedPeopleColumn;
    @FXML
    public TableColumn<Map,String> activityOrganizeUnitColumn;
    @FXML
    public TableColumn<Map,String> qualityDevelopmentCreditColumn;
    @FXML
    public TextField nameField;
    @FXML
    public TextField addressField;

    @FXML
    public TextField volunteertimeField;
    @FXML
    public TextField studentField;
    @FXML
    public TextField studentnameField;
    @FXML
    public TextField joinedPeopleField;
    @FXML
    public TextField activityOrganizeUnitField;
    @FXML
    public TextField qualityDevelopmentCreditField;
    @FXML
    public ComboBox<OptionItem> HDSPComboBox;
    @FXML
    public TextField numNameTextField;  //查询 姓名学号输入域

    private Integer activityId = null;  //当前编辑修改的教师的主键
    private List<OptionItem> HDSPList;   //选择列表数据
    private ArrayList<Map> allUserList;
    private ArrayList<Map> activityList = new ArrayList();  // 活动信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表


    /**
     * 将活动数据集合设置到面板上显示
     */
    //question
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < activityList.size(); j++) {
            if(show == 2 && (activityList.get(j).get("HDSPState_name").equals("已通过")||activityList.get(j).get("HDSPState_name").equals("未通过"))) {
                continue;
            }
            observableList.addAll(FXCollections.observableArrayList(activityList.get(j)));
        }
        dataTableView.setItems(observableList);
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        studentField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean ok = false;
            for(int i = 0; i < allUserList.size();i++){
                String thisNum = allUserList.get(i).get("num").toString();
                if(thisNum.equals(newValue)){
                    personNameLabel.setText(allUserList.get(i).get("name").toString());
                    ok = true;
                    break;
                }
            }
            if(ok == false)
                personNameLabel.setText("");
        });
        DataResponse res;
        DataResponse res2;
        DataRequest req =new DataRequest();
        DataRequest req2=new DataRequest();
        req.add("activityId","");
        res = HttpRequestUtil.request("/api/activity/initialize",req); //从后台获取所有活动信息列表集合
        res2 = HttpRequestUtil.request("/api/achievement/getUserList",req2);
        if(res2 != null && res2.getCode()== 0) {
            allUserList = (ArrayList<Map>)res2.getData();
        }
        if(res != null && res.getCode()== 0) {
            activityList = (ArrayList<Map>)res.getData();
        }
        leftPeopleRemainingColumn.setCellValueFactory(new MapValueFactory<>("leftPeopleRemaining"));
        personNameColumn.setCellValueFactory(new MapValueFactory<>("personName"));
        nameColumn.setCellValueFactory(new MapValueFactory<>("name"));
        addressColumn.setCellValueFactory(new MapValueFactory<>("address"));
        timestartColumn.setCellValueFactory(new MapValueFactory<>("timeStart"));
        timeendColumn.setCellValueFactory(new MapValueFactory<>("timeEnd"));
        volunteertimeColumn.setCellValueFactory(new MapValueFactory<>("volunteerTime"));
        studentColumn.setCellValueFactory(new MapValueFactory<>("studentNum"));
        joinedPeopleColumn.setCellValueFactory(new MapValueFactory<>("joinedPeople"));
        activityOrganizeUnitColumn.setCellValueFactory(new MapValueFactory<>("activityOrganizeUnit"));
        qualityDevelopmentCreditColumn.setCellValueFactory(new MapValueFactory<>("qualityDevelopmentCredit"));
        HDSPColumn.setCellValueFactory(new MapValueFactory<>("HDSPState_name"));
        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
        ObservableList<Integer> list = tsm.getSelectedIndices();
        list.addListener(this::onTableRowSelect);
        HDSPList = HttpRequestUtil.getDictionaryOptionItemList("HDSP");
        HDSPComboBox.getItems().addAll(HDSPList);
        timestartPicker.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        timeendPicker.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
        setTableViewData();
        locked();
    }
    public boolean isNumber(String str){
        char[] array = str.toCharArray();
        for(int i = 0; i < array.length ; i++){
            if(array[i]<'0'||array[i]>'9')
                return false;
        }
        return true;
    }
    private void locked2(){
        saveButton.setDisable(true);
    }
    private void locked(){
//        beginTimeDatePicker.display
        HDSPComboBox.setDisable(true);
    }
    /**
     * 清除教师表单中输入信息
     */
    public void clearPanel(){
        activityId = null;
        personNameLabel.setText("");
        leftPeopleRemainingLabel.setText("");
        nameField.setText("");
        addressField.setText("");
        volunteertimeField.setText("");
        studentField.setText("");
        studentnameField.setText("");
        joinedPeopleField.setText("");
        activityOrganizeUnitField.setText("");
        qualityDevelopmentCreditField.setText("");
        HDSPComboBox.getSelectionModel().select(-1);
        timestartPicker.getEditor().setText("");
        timeendPicker.getEditor().setText("");
        joinedPeopleField.setDisable(false);
        dataTableView.getSelectionModel().clearSelection();
    }

    public void changeActivityInfo() {
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            clearPanel();
            return;
        }
        activityId = CommonMethod.getInteger(form,"activityId");
        DataRequest req = new DataRequest();
        req.add("activityId",activityId);
        DataResponse res;
        res = HttpRequestUtil.request("/api/activity/initialize",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        activityList = (ArrayList<Map>)res.getData();
        Map form2=activityList.get(0);
        leftPeopleRemainingLabel.setText(CommonMethod.getString(form2,"leftPeopleRemaining"));
        personNameLabel.setText(CommonMethod.getString(form2,"personName"));
        nameField.setText(CommonMethod.getString(form2, "name"));
        addressField.setText(CommonMethod.getString(form2, "address"));
        volunteertimeField.setText(CommonMethod.getString(form2, "volunteerTime"));
        studentField.setText(CommonMethod.getString(form2, "studentNum"));
        joinedPeopleField.setText(CommonMethod.getString(form2, "joinedPeople"));
        activityOrganizeUnitField.setText(CommonMethod.getString(form2, "activityOrganizeUnit"));
        qualityDevelopmentCreditField.setText(CommonMethod.getString(form2, "qualityDevelopmentCredit"));
        HDSPComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(HDSPList, CommonMethod.getString(form2, "HDSPState_name")));
        timestartPicker.getEditor().setText(CommonMethod.getString(form2, "timeStart"));
        timeendPicker.getEditor().setText(CommonMethod.getString(form2, "timeEnd"));
        joinedPeopleField.setDisable(true);

    }
    /**
     * 点击教师列表的某一行，根据teacherId ,从后台查询教师的基本信息，切换教师的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeActivityInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的教师在教师列表中显示
     */
    @FXML
    public void onQueryButtonClick() {//finished
        String studentNum = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("studentNumAndName",studentNum);
        DataResponse res = HttpRequestUtil.request("/api/activity/getFuzzyActivity",req);
        if(res != null && res.getCode()== 0) {
            activityList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }
    @FXML
    public void reLoading() {//finished
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/activity/initialize",req);
        if(res != null && res.getCode()== 0) {
            activityList = (ArrayList<Map>)res.getData();
            setTableViewData();
        }
    }

    /**
     *  添加新教师， 清空输入信息， 输入相关信息，点击保存即可添加新的教师
     */
    @FXML
    public void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除当前编辑的教师的数据
     */
    @FXML
    public void onDeleteButtonClick() {    //have finished
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        activityId = CommonMethod.getInteger(form,"activityId");
        DataRequest req = new DataRequest();
        req.add("activityId", activityId);
        DataResponse res = HttpRequestUtil.request("/api/activity/deleteActivity",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            reLoading();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    @FXML
    public void onRemakeButtonClick(){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form.get("activityId").equals("")){
            return;
        }
        DataRequest req = new DataRequest();
        String activityId=CommonMethod.getString(form,"activityId");
        req.add("activityId", activityId);
        req.add("state",3);
        DataResponse res = HttpRequestUtil.request("/api/activity/modifyState",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("审批成功！");
            reLoading();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    @FXML
    public void onPassButtonClick(){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form.get("activityId").equals("")){
            return;
        }
        DataRequest req = new DataRequest();
        String activityId=CommonMethod.getString(form,"activityId");
        req.add("activityId", activityId);
        req.add("state",2);
        DataResponse res = HttpRequestUtil.request("/api/activity/modifyState",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("审批成功！");
            reLoading();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    /**
     * 点击保存按钮，保存当前编辑的教师信息，如果是新添加的教师，后台添加教师
     */
    @FXML
    public void onSaveButtonClick() {
        Map form2 = dataTableView.getSelectionModel().getSelectedItem();
        Map form = new HashMap();
        if(joinedPeopleField.getText()==""||timestartPicker.getEditor().getText()==""||timeendPicker.getEditor().getText()==""){
            MessageDialog.showDialog("请输入正确的信息");
            return;
        }
        if(form2!=null){
            double ai=(double) form2.get("activityId");
            activityId = (int)ai;
            form.put("leftPeopleRemaining",leftPeopleRemainingLabel.getText());
            form.put("personName",personNameLabel.getText());
            form.put("name",nameField.getText());
            form.put("address",addressField.getText());
            form.put("timeStart",timestartPicker.getEditor().getText());
            form.put("timeEnd",timeendPicker.getEditor().getText());
            form.put("volunteerTime",volunteertimeField.getText());
            //form.put("studentNum",studentField.getText());
            form.put("joinedPeople",joinedPeopleField.getText());
            form.put("activityOrganizeUnit",activityOrganizeUnitField.getText());
            form.put("qualityDevelopmentCredit",qualityDevelopmentCreditField.getText());
            DataRequest req = new DataRequest();
            req.add("activityId", (Integer)activityId);
            req.add("form", form);
            String beginDayStr = timestartPicker.getEditor().getText();
            String endDayStr = timeendPicker.getEditor().getText();
            LocalDate nowDay = LocalDate.now();
            LocalDate beginDay = LocalDate.parse(beginDayStr);
            LocalDate endDay = LocalDate.parse(endDayStr);
            Long days = beginDay.until(endDay, ChronoUnit.DAYS);
            Long untilNow = nowDay.until(beginDay , ChronoUnit.DAYS);
            if(days < 0){
                MessageDialog.showDialog("结束日期不能早于开始日期！");
                return;
            }
            if(untilNow < 0){
                MessageDialog.showDialog("开始日期不能早于今天！");
                return;
            }
            DataResponse res = HttpRequestUtil.request("/api/activity/editActivity",req);
            if(res.getCode() == 0) {
                activityId = CommonMethod.getIntegerFromObject(res.getData());
                MessageDialog.showDialog("提交成功！");
                reLoading();
            }
            else {
                MessageDialog.showDialog(res.getMsg());
            }
        }
        else{

            form.put("leftPeopleRemaining",leftPeopleRemainingLabel.getText());
            form.put("personName",personNameLabel.getText());
            form.put("name",nameField.getText());
            form.put("address",addressField.getText());
            form.put("timeStart",timestartPicker.getEditor().getText());
            form.put("timeEnd",timeendPicker.getEditor().getText());
            form.put("volunteerTime",volunteertimeField.getText());
            //form.put("studentNum",studentField.getText());
            form.put("joinedPeople",joinedPeopleField.getText());
            form.put("activityOrganizeUnit",activityOrganizeUnitField.getText());
            form.put("qualityDevelopmentCredit",qualityDevelopmentCreditField.getText());
            DataRequest req = new DataRequest();
            req.add("studentNum",studentField.getText());
            req.add("activityId", (Integer)activityId);
            req.add("form", form);
            String beginDayStr = timestartPicker.getEditor().getText();
            String endDayStr = timeendPicker.getEditor().getText();
            LocalDate nowDay = LocalDate.now();
            LocalDate beginDay = LocalDate.parse(beginDayStr);
            LocalDate endDay = LocalDate.parse(endDayStr);
            Long days = beginDay.until(endDay, ChronoUnit.DAYS);
            Long untilNow = nowDay.until(beginDay , ChronoUnit.DAYS);
            if(days < 0){
                MessageDialog.showDialog("结束日期不能早于开始日期！");
                return;
            }
            if(untilNow < 0){
                MessageDialog.showDialog("开始日期不能早于今天！");
                return;
            }
            DataResponse res = HttpRequestUtil.request("/api/activity/addActivity",req);
            if(res.getCode() == 0) {
                activityId = CommonMethod.getIntegerFromObject(res.getData());
                MessageDialog.showDialog("提交成功！");
                reLoading();
            }
            else {
                MessageDialog.showDialog(res.getMsg());
            }
        }

        clearPanel();
    }
    @FXML
    public void changeShow(){
        if(show == 1){
            show = 2;
            showButton.setText("显示全部数据");
        }
        else if(show == 2){
            show = 1;
            showButton.setText("只显示待审批");
        }
        reLoading();
    }

    /**
     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对教师的增，删，改操作
     */
    public void doNew(){
        clearPanel();
    }
    public void doSave(){
        onSaveButtonClick();
    }
    public void doDelete(){
        onDeleteButtonClick();
    }

    /**
     * 导出活动信息表的示例 重写ToolController 中的doExport 这里给出了一个导出教师基本信息到Excl表的示例， 后台生成Excl文件数据，传回前台，前台将文件保存到本地
     */

}
