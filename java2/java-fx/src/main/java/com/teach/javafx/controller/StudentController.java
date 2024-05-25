package com.teach.javafx.controller;

import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.factories.StudentValueFactory;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.models.Student;
import com.teach.javafx.request.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.util.Callback;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import com.teach.javafx.controller.base.MessageDialog;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


/**
 * StudentController 登录交互控制类 对应 student_panel.fxml  对应于学生管理的后台业务处理的控制器，主要获取数据和保存数据的方法不同
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 */
public class StudentController extends ToolController {
    @FXML
    private TableView<Student> dataTableView;  //学生信息表
    @FXML
    private TableColumn<Student,String> numColumn;   //学生信息表 编号列
    @FXML
    private TableColumn<Student,String> nameColumn; //学生信息表 名称列
    @FXML
    private TableColumn<Student,String> deptColumn;  //学生信息表 院系列
    @FXML
    private TableColumn<Student,String> majorColumn; //学生信息表 专业列
    @FXML
    private TableColumn<Student,String> classNameColumn; //学生信息表 班级列
    @FXML
    private TableColumn<Student,String> cardColumn; //学生信息表 证件号码列
    @FXML
    private TableColumn<Student,String> genderColumn; //学生信息表 性别列
    @FXML
    private TableColumn<Student,String> birthdayColumn; //学生信息表 出生日期列
    @FXML
    private TableColumn<Student,String> emailColumn; //学生信息表 邮箱列
    @FXML
    private TableColumn<Student,String> phoneColumn; //学生信息表 电话列
    @FXML
    private TableColumn<Student,String> addressColumn;//学生信息表 地址列

//    @FXML
//    private TextField numField; //学生信息  学号输入域
//    @FXML
//    private TextField nameField;  //学生信息  名称输入域
//    @FXML
//    private TextField deptField; //学生信息  院系输入域
//    @FXML
//    private TextField majorField; //学生信息  专业输入域
//    @FXML
//    private TextField classNameField; //学生信息  班级输入域
//    @FXML
//    private TextField cardField; //学生信息  证件号码输入域
//    @FXML
//    private ComboBox<OptionItem> genderComboBox;  //学生信息  性别输入域
//    @FXML
//    private DatePicker birthdayPick;  //学生信息  出生日期选择域
//    @FXML
//    private TextField emailField;  //学生信息  邮箱输入域
//    @FXML
//    private TextField phoneField;   //学生信息  电话输入域
//    @FXML
//    private TextField addressField;  //学生信息  地址输入域

    @FXML
    private TextField numNameTextField;  //查询 姓名学号输入域

    private Integer studentId = null;  //当前编辑修改的学生的主键

    private ArrayList<Student> studentList = new ArrayList();  // 学生信息列表数据
    private List<OptionItem> genderList;   //性别选择列表数据


    /**
     * 将学生数据集合设置到面板上显示
     */
    private void setTableViewData() {
        dataTableView.getItems().clear();
        dataTableView.setItems(FXCollections.observableArrayList(studentList));
    }

    /**
     * 页面加载对象创建完成初始化方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */

    @FXML
    public void initialize() {
        onQueryButtonClick();

        numColumn.setCellValueFactory(new StudentValueFactory());  //设置列值工厂属性
        nameColumn.setCellValueFactory(new StudentValueFactory());
        deptColumn.setCellValueFactory(new StudentValueFactory());
        majorColumn.setCellValueFactory(new StudentValueFactory());
        classNameColumn.setCellValueFactory(new StudentValueFactory());
        cardColumn.setCellValueFactory(new StudentValueFactory());
        genderColumn.setCellValueFactory(new StudentValueFactory());
        birthdayColumn.setCellValueFactory(new StudentValueFactory());
        emailColumn.setCellValueFactory(new StudentValueFactory());
        phoneColumn.setCellValueFactory(new StudentValueFactory());
        addressColumn.setCellValueFactory(new StudentValueFactory());
        //这段代码是为了让每一行被点击时，旁边的编辑面板能发生相应的变化，即显示被选中的项的信息
//        TableView.TableViewSelectionModel<Map> tsm = dataTableView.getSelectionModel();
//        ObservableList<Integer> list = tsm.getSelectedIndices();
//        list.addListener(this::onTableRowSelect);

        setTableViewData();
        genderList = HttpRequestUtil.getDictionaryOptionItemList("XBM");

//        genderComboBox.getItems().addAll(genderList);
//        birthdayPick.setConverter(new LocalDateStringConverter("yyyy-MM-dd"));
    }

    //从后端获取信息


    /**
     * 清除学生表单中输入信息
     */
//    public void clearPanel(){
//        studentId = null;
//        numField.setText("");
//        nameField.setText("");
//        deptField.setText("");
//        majorField.setText("");
//        classNameField.setText("");
//        cardField.setText("");
//        genderComboBox.getSelectionModel().select(-1);
//        birthdayPick.getEditor().setText("");
//        emailField.setText("");
//        phoneField.setText("");
//        addressField.setText("");
//    }

//    protected void changeStudentInfo() {
//        Map form = dataTableView.getSelectionModel().getSelectedItem();
//        if(form == null) {
//            clearPanel();
//            return;
//        }
//        studentId = CommonMethod.getInteger(form,"studentId");
//        DataRequest req = new DataRequest();
//        req.add("studentId",studentId);
//        DataResponse res = HttpRequestUtil.request("/api/student/getStudentInfo",req);
//        if(res.getCode() != 0){
//            MessageDialog.showDialog(res.getMsg());
//            return;
//        }
//        form = (Map)res.getData();
//        numField.setText(CommonMethod.getString(form, "studentNum"));
//        nameField.setText(CommonMethod.getString(form, "studentName"));
//        deptField.setText(CommonMethod.getString(form, "dept"));
//        majorField.setText(CommonMethod.getString(form, "major"));
//        classNameField.setText(CommonMethod.getString(form, "className"));
//        cardField.setText(CommonMethod.getString(form, "card"));
//        genderComboBox.getSelectionModel().select(CommonMethod.getOptionItemIndexByValue(genderList, CommonMethod.getString(form, "gender")));
//        birthdayPick.getEditor().setText(CommonMethod.getString(form, "birthday"));
//        emailField.setText(CommonMethod.getString(form, "email"));
//        phoneField.setText(CommonMethod.getString(form, "phone"));
//        addressField.setText(CommonMethod.getString(form, "address"));
//
//    }
    /**
     * 点击学生列表的某一行，根据studentId ,从后台查询学生的基本信息，切换学生的编辑信息
     */

//    public void onTableRowSelect(ListChangeListener.Change<? extends Integer> change){
//        changeStudentInfo();
//    }

    /**
     * 点击查询按钮，从从后台根据输入的串，查询匹配的学生在学生列表中显示
     */
    @FXML
    protected void onQueryButtonClick() {
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName",numName);
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentList",req);
        if(res != null && res.getCode()== 0) {
            List<Map> rawData = (ArrayList<Map>)res.getData();
            studentList.clear();
            for(Map m : rawData){
                Student s = new Student(m);
                studentList.add(s);
            }
            setTableViewData();
        }

    }

    /**
     *  添加新学生， 清空输入信息， 输入相关信息，点击保存即可添加新的学生
     */
//    @FXML
//    protected void onAddButtonClick() {
//        clearPanel();
//    }

    public void onAddButtonClick(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("student-editor.fxml"));
             Stage stage= WindowsManager.getInstance().openNewWindow(

                    loader, 460, 700, "新增学生",

                    dataTableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            StudentEditorController seCont = (StudentEditorController) controller;
                            seCont.init(null,StudentController.this);
                        }
                    }
            );
             stage.setResizable(false);
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开新增页面失败");
        }
    }

    /**
     * 点击删除按钮 删除当前编辑的学生的数据
      */
    @FXML
    protected void onDeleteButtonClick() {
        Student student = dataTableView.getSelectionModel().getSelectedItem();
        if(student == null) {
            MessageDialog.showDialog("没有选择，不能删除");
            return;
        }
        int ret = MessageDialog.choiceDialog("确认要删除吗?");
        if(ret != MessageDialog.CHOICE_YES) {
            return;
        }
        Integer studentId = student.getStudentId();
        System.out.println("要删除的学生ID: " + studentId);
        DataRequest req = new DataRequest();
        req.add("studentId", studentId);
        DataResponse res = HttpRequestUtil.request("/api/student/studentDelete",req);
        System.out.println("请求状态: " + res.getCode());
        assert res != null;
        if(res.getCode() == 0) {
            MessageDialog.showDialog("删除成功！");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void onEditButtonClick(ActionEvent event){
        Student s = dataTableView.getSelectionModel().getSelectedItem();
        if(s == null){
            MessageDialog.showDialog("请选择学生后再编辑!");
            return;
        }
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("student-editor.fxml"));
        try{
            Stage stage= WindowsManager.getInstance().openNewWindow(

                    loader, 460, 700, "编辑: " + s.getStudentName(),

                    dataTableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            StudentEditorController seCont = (StudentEditorController) controller;
                            seCont.init(s, StudentController.this);
                        }
                    }
            );
            stage.setResizable(false);
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法打开编辑页面");
        }
    }

    //查看家庭成员
    public void onViewFamilyMember(){
        Student s = dataTableView.getSelectionModel().getSelectedItem();
        if(s == null){
            MessageDialog.showDialog("请选中学生以查看其家庭成员");
            return;
        }
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("family-member.fxml"));
            WindowsManager.getInstance().openNewWindow(
                    loader, 800, 600, s.getStudentName() + " 的家庭成员",
                    dataTableView.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            FamilyMemberController fmCont = (FamilyMemberController) controller;
                            fmCont.init(s);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("无法查看家庭成员");
        }
    }

    /**
     * 点击保存按钮，保存当前编辑的学生信息，如果是新添加的学生，后台添加学生
     */
//    @FXML
//    protected void onSaveButtonClick() {
//        if( numField.getText().equals("")) {
//            MessageDialog.showDialog("学号为空，不能修改");
//            return;
//        }
//
//        //判断邮箱是否合法，不合法就拒绝提交并弹出提示
//        if(!CommonMethod.isValidEmail(emailField.getText()) && (emailField.getText() != null && !emailField.getText().isEmpty())){
//            MessageDialog.showDialog("邮箱格式不合法!");
//            return;
//        }
//        Map form = new HashMap();
//        form.put("personNum",numField.getText());
//        form.put("personName",nameField.getText());
//        form.put("dept",deptField.getText());
//        form.put("major",majorField.getText());
//        form.put("className",classNameField.getText());
//        form.put("card",cardField.getText());
//        if(genderComboBox.getSelectionModel() != null && genderComboBox.getSelectionModel().getSelectedItem() != null)
//           form.put("gender",genderComboBox.getSelectionModel().getSelectedItem().getValue());
//        form.put("birthday",birthdayPick.getEditor().getText());
//        form.put("email",emailField.getText());
//        form.put("phone",phoneField.getText());
//        form.put("address",addressField.getText());
//        DataRequest req = new DataRequest();
//        req.add("studentId", studentId);
//        req.add("form", form);
//        DataResponse res = HttpRequestUtil.request("/api/student/studentEditSave",req);
//        if(res.getCode() == 0) {
//            studentId = CommonMethod.getIntegerFromObject(res.getData());
//            MessageDialog.showDialog("提交成功！");
//            onQueryButtonClick();
//        }
//        else {
//            MessageDialog.showDialog(res.getMsg());
//        }
//    }
//
//    /**
//     * doNew() doSave() doDelete() 重写 ToolController 中的方法， 实现选择 新建，保存，删除 对学生的增，删，改操作
//     */
//    public void doNew(){
//        clearPanel();
//    }
//    public void doSave(){
//        onSaveButtonClick();
//    }
    public void doDelete(){
        onDeleteButtonClick();
    }

    /**
     * 导出学生信息表的示例 重写ToolController 中的doExport 这里给出了一个导出学生基本信息到Excl表的示例， 后台生成Excl文件数据，传回前台，前台将文件保存到本地
     */
    public void doExport(){
        String numName = numNameTextField.getText();
        DataRequest req = new DataRequest();
        req.add("numName",numName);
        byte[] bytes = HttpRequestUtil.requestByteData("/api/student/getStudentListExcl", req);
        if (bytes != null) {
            try {
                FileChooser fileDialog = new FileChooser();
                fileDialog.setTitle("前选择保存的文件");
                fileDialog.setInitialDirectory(new File("C:/"));
                fileDialog.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
                File file = fileDialog.showSaveDialog(null);
                if(file != null) {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(bytes);
                    out.close();
                    MessageDialog.showDialog("已保存至: " + file.getPath());
                }
            }catch(Exception e){
                e.printStackTrace();
                MessageDialog.showDialog("保存失败: 请检查文件夹权限!");
            }
        }
        else{
            MessageDialog.showDialog("无法获取学生表! ");
        }

    }
    @FXML
    protected void onImportButtonClick() {
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("请选择学生数据表");
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        List<Path> pathList = new ArrayList<>();
        pathList.add(file.toPath());
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/student/importStudentExcel",req,pathList);
        if(res.getCode() == 0) {
            Map rawData = (Map)res.getData();
            int total = CommonMethod.getInteger(rawData,"total");
            int success = CommonMethod.getInteger(rawData,"success");
            MessageDialog.showDialog("上传完成! \n学生总数: " + total + " 个\n成功上传: " + success + " 个");
            onQueryButtonClick();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void hasSaves() {
        onQueryButtonClick();
    }
}
