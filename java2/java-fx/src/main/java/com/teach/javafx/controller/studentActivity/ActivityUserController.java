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

public class ActivityUserController extends ToolController {
    @FXML
    private TableColumn HDSPColumn;
    @FXML
    private Button saveButton;
    @FXML
    private Button partButton;
    @FXML
    private Label personNameLabel;
    @FXML
    private Label leftPeopleRemainingLabel;
    private Integer show = 1;
    @FXML
    private Button showButton;
    @FXML
    private Button passButton;
    @FXML
    private Button remakeButton;
    @FXML
    private DatePicker timestartPicker;  //教师信息  出生日期选择域
    @FXML
    private DatePicker timeendPicker;  //教师信息  出生日期选择域
    @FXML
    private TableView<Map> dataTableView;
    @FXML
    private TableColumn<Map,String> nameColumn;
    @FXML
    private TableColumn<Map,String> addressColumn;
    @FXML
    private TableColumn<Map,String> leftPeopleRemainingColumn;
    @FXML
    private TableColumn<Map,String> personNameColumn;
    @FXML
    private TableColumn<Map,String> timestartColumn; //教师信息表 出生日期列
    @FXML
    private TableColumn<Map,String> timeendColumn; //教师信息表 出生日期列
    @FXML
    private TableColumn<Map,String> volunteertimeColumn;
    @FXML
    private TableColumn<Map,String> studentColumn;
    @FXML
    private TableColumn<Map,String> joinedPeopleColumn;
    @FXML
    private TableColumn<Map,String> activityOrganizeUnitColumn;
    @FXML
    private TableColumn<Map,String> qualityDevelopmentCreditColumn;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;

    @FXML
    private TextField volunteertimeField;
    @FXML
    private TextField studentField;
    @FXML
    public TextField studentnameField;
    @FXML
    private TextField joinedPeopleField;
    @FXML
    private TextField activityOrganizeUnitField;
    @FXML
    private TextField qualityDevelopmentCreditField;
    @FXML
    private ComboBox<OptionItem> HDSPComboBox;
    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer activityId = null;  //当前编辑修改的教师的主键
    private List<OptionItem> HDSPList;   //选择列表数据
    private ArrayList<Map> allUserList;
    private ArrayList<Map> activityList = new ArrayList();  // 教师信息列表数据
    private ObservableList<Map> observableList= FXCollections.observableArrayList();  // TableView渲染列表


    /**
     * 将教师数据集合设置到面板上显示
     */
    //question
    private void setTableViewData() {
        observableList.clear();
        for (int j = 0; j < activityList.size(); j++) {
            if(show == 2 && activityList.get(j).get("HDSPState_name").equals("1"))continue;
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
            if(ok == false)personNameLabel.setText("");
        });
        DataRequest req3 = new DataRequest();
        DataResponse res3=HttpRequestUtil.request("/api/activity/getStudentNumAndName",req3);
        Map form2 =(Map)res3.getData();
        String a=CommonMethod.getString(form2, "studentNum");
        String b=CommonMethod.getString(form2,"name");
        studentField.setText(a);
        personNameLabel.setText(b);
        studentField.setDisable(true);
        DataResponse res;
        DataResponse res2;
        DataRequest req =new DataRequest();
        DataRequest req2=new DataRequest();
//        req.put("activityId","");
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
    private void locked(){
//        beginTimeDatePicker.display
        HDSPComboBox.setDisable(true);
    }
    private void locked2(){
        saveButton.setDisable(true);
    }
    private  void unlocked2(){
        saveButton.setDisable(false);
    }
    /**
     * 清除教师表单中输入信息
     */
    public void clearPanel(){
        DataRequest req = new DataRequest();
        DataResponse res=HttpRequestUtil.request("/api/activity/getStudentNumAndName",req);
        Map form2 =(Map)res.getData();
        String a=CommonMethod.getString(form2, "studentNum");
        String b=CommonMethod.getString(form2, "name");
        activityId = null;
        personNameLabel.setText(b);
        leftPeopleRemainingLabel.setText("");
        nameField.setText("");
        addressField.setText("");
        volunteertimeField.setText("");
        studentField.setText(a);
        studentnameField.setText(a);
        joinedPeopleField.setText("");
        activityOrganizeUnitField.setText("");
        qualityDevelopmentCreditField.setText("");
        HDSPComboBox.getSelectionModel().select(-1);
        timestartPicker.getEditor().setText("");
        timeendPicker.getEditor().setText("");
        partButton.setText("我要参与");
        joinedPeopleField.setDisable(false);
        dataTableView.getSelectionModel().clearSelection();
    }

    protected void changeactivityInfo() {
        partButton.setText("我要参与");
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form == null) {
            unlocked2();
            clearPanel();
            return;
        }
        DataRequest req5=new DataRequest();
        DataResponse res5=HttpRequestUtil.request("/api/activity/getStudentId",req5);

            double ab=(Double) res5.getData();
        Integer studentId = (int)ab;
        DataRequest req3=new DataRequest();
        req3.add("studentId",studentId);
        DataResponse res3=HttpRequestUtil.request("/api/activity/getStudentActivityList",req3);
        List aa=(List) res3.getData();

        activityId = CommonMethod.getInteger(form,"activityId");
        DataRequest req = new DataRequest();
        req.add("activityId",activityId);
        Boolean part=false;
        for(int i=0;i< aa.size();i++){
            Map map=(Map)aa.get(i);
            double ai= (double) map.get("activityId");
            if((int) ai==activityId){
                part=true;
                partButton.setText("您已参与");
            }

        }
        DataResponse res = HttpRequestUtil.request("/api/activity/initialize",req);
        if(res.getCode() != 0){
            MessageDialog.showDialog(res.getMsg());
            return;
        }
        activityList = (ArrayList<Map>)res.getData();
        Map form3=activityList.get(0);
        leftPeopleRemainingLabel.setText(CommonMethod.getString(form3,"leftPeopleRemaining"));
        personNameLabel.setText(CommonMethod.getString(form3,"personName"));
        nameField.setText(CommonMethod.getString(form3, "name"));
        addressField.setText(CommonMethod.getString(form3, "address"));
        volunteertimeField.setText(CommonMethod.getString(form3, "volunteerTime"));
        studentField.setText(CommonMethod.getString(form3, "studentNum"));
        joinedPeopleField.setText(CommonMethod.getString(form3, "joinedPeople"));
        activityOrganizeUnitField.setText(CommonMethod.getString(form3, "activityOrganizeUnit"));
        qualityDevelopmentCreditField.setText(CommonMethod.getString(form3, "qualityDevelopmentCredit"));
        HDSPComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(HDSPList, CommonMethod.getString(form3, "HDSPState_name")));
        timestartPicker.getEditor().setText(CommonMethod.getString(form3, "timeStart"));
        timeendPicker.getEditor().setText(CommonMethod.getString(form3, "timeEnd"));
        joinedPeopleField.setDisable(true);
    }
    /**
     * 点击活动列表的某一行，根据activityId ,从后台查询活动的基本信息，切换活动的编辑信息
     */

    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
        changeactivityInfo();
    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的活动在活动列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {//finished
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
    protected void reLoading() {//finished
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
    protected void onAddButtonClick() {
        clearPanel();
    }

    /**
     * 点击删除按钮 删除当前编辑的教师的数据
     */
    @FXML
    protected void onDeleteButtonClick() {    //have finished
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        DataResponse resp;
        DataRequest requ=new DataRequest();
        resp = HttpRequestUtil.request("/api/activity/getStudentNumAndName",requ);
        Map form3 =(Map)resp.getData();
        String Num=CommonMethod.getString(form3, "studentNum");
        if(form!=null){
            if(!Num.equals(form.get("studentNum"))){
                MessageDialog.showDialog("没有权限");
                return;
            }
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
    }}
    @FXML
    protected void onRemakeButtonClick(){
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
    protected void onPartButtonClick(){
        Map form = dataTableView.getSelectionModel().getSelectedItem();
        if(form.get("activityId").equals("")){
            return;
        }
        DataRequest req = new DataRequest();
        DataRequest req2 = new DataRequest();
        DataResponse res2 = HttpRequestUtil.request("/api/activity/getStudentId",req2);
        Integer activityId=CommonMethod.getInteger(form,"activityId");
        Double studentId= (Double) res2.getData();
        req.add("activityId", activityId);

        req.add("studentId",(int)Math.round(studentId));
        DataResponse res = HttpRequestUtil.request("/api/studentActivity/addStudentActivity",req);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("报名成功！");
            reLoading();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    @FXML
    protected void onPassButtonClick(){
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
    protected void onSaveButtonClick() {
        Map form2 = dataTableView.getSelectionModel().getSelectedItem();
        Map form = new HashMap();
        DataResponse resp;
        DataRequest requ=new DataRequest();
        resp = HttpRequestUtil.request("/api/activity/getStudentNumAndName",requ);
        Map form3 =(Map)resp.getData();
        String Num=CommonMethod.getString(form3, "studentNum");
        if(form2!=null){
            if(!Num.equals(form2.get("studentNum"))){
                MessageDialog.showDialog("没有权限");
                return;
            }
            if(joinedPeopleField.getText()==""||timestartPicker.getEditor().getText()==""||timeendPicker.getEditor().getText()==""){
                MessageDialog.showDialog("请输入正确的信息");
                return;
            }
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
            if(joinedPeopleField.getText()==""||timestartPicker.getEditor().getText()==""||timeendPicker.getEditor().getText()==""){
                MessageDialog.showDialog("请输入正确的信息");
                return;
            }
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
            DataResponse res = HttpRequestUtil.request("/api/activity/addActivity2",req);
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
    protected void changeShow(){
        if(show == 1){
            show = 2;
            showButton.setText("显示全部数据");
        }
        else if(show == 2){
            show = 1;
            showButton.setText("只显示待审批");
        }
        setTableViewData();

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
     * 导出教师信息表的示例 重写ToolController 中的doExport 这里给出了一个导出教师基本信息到Excl表的示例， 后台生成Excl文件数据，传回前台，前台将文件保存到本地
     */
}
