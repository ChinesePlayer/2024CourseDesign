package com.teach.javafx.controller;

import com.teach.javafx.AppStore;
import com.teach.javafx.MainApplication;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.base.ToolController;
import com.teach.javafx.controller.studentDraw.StudentDrawController;
import com.teach.javafx.factories.StudentScoreValueFactory;
import com.teach.javafx.managers.WindowOpenAction;
import com.teach.javafx.managers.WindowsManager;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.fxml.FXMLLoader;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * StudentIntroduceController 登录交互控制类 对应 student-introduce-panel..fxml
 *  @FXML  属性 对应fxml文件中的
 *  @FXML 方法 对应于fxml文件中的 on***Click的属性
 *  该功能是为学生提供生成个人简历的是示例成程序，继承了多种布局容器和多种组件，集成PDF生成转换技术。学生可以在此程序的基础上进行修改，扩展，
 *  在简单熟悉HTML的基础上，构建比较美观丰富的个人简历，并生成和下载PDF文件
 */
public class StudentIntroduceController extends ToolController {
    public ImageView photoImageView;
    public ObservableList<Score> observableList= FXCollections.observableArrayList();
    public List<Score> scoreList = new ArrayList<>();
    public List<Map> feeList = new ArrayList<>();
    public List<Map> markList = new ArrayList<>();

    @FXML
    public Button photoButton;  //照片显示和上传按钮
    @FXML
    public Label num;  //学号标签
    @FXML
    public Label name;//姓名标签
    @FXML
    public Label dept; //学院标签
    @FXML
    public Label major; //专业标签
    @FXML
    public Label className; //班级标签
    @FXML
    public Label card;  //证件号码标签
    @FXML
    public Label gender; //性别标签
    @FXML
    public Label birthday; //出生日期标签
    @FXML
    public Label email; //邮箱标签
    @FXML
    public Label phone; //电话标签
    @FXML
    public Label address; //地址标签
    @FXML
    public TableView<Score> scoreTable;  //成绩表TableView
    @FXML
    public TableColumn<Score,String> courseNum;  //课程号列
    @FXML
    public TableColumn<Score,String> courseName; //课程名列
    @FXML
    public TableColumn<Score,String> credit; //学分列
    @FXML
    public TableColumn<Score,String> mark; //成绩列
    @FXML
    public TableColumn<Score,String> rank; //排名列

    @FXML
    public BarChart<String,Number> barChart;  //消费直方图控件
    @FXML
    public PieChart pieChart;   //成绩分布饼图控件
    public Integer studentId = null;  //学生主键
    public Integer personId = null;  //学生关联人员主键

    /**
     * 页面加载对象创建完成初始话方法，页面中控件属性的设置，初始数据显示等初始操作都在这里完成，其他代码都事件处理方法里
     */
    @FXML
    public void initialize() {
        photoImageView = new ImageView();
        photoImageView.setFitHeight(100);
        photoImageView.setFitWidth(100);
        photoButton.setGraphic(photoImageView);
        courseNum.setCellValueFactory(new StudentScoreValueFactory());
        courseName.setCellValueFactory(new StudentScoreValueFactory());
        credit.setCellValueFactory(new StudentScoreValueFactory());
        mark.setCellValueFactory(new StudentScoreValueFactory());
        rank.setCellValueFactory(new StudentScoreValueFactory());

        getIntroduceData();
    }

    public void initScoreList(List<Map> rawData){
        scoreList.clear();
        for(Map m : rawData){
            Score s = new Score(m);
            scoreList.add(s);
        }
    }

    public void setScoreTable(){
        observableList.clear();
        observableList.addAll(FXCollections.observableArrayList(scoreList));
        scoreTable.setItems(observableList);
    }


    /**
     * getIntroduceData 从后天获取当前学生的所有信息，不传送的面板各个组件中
     */
    public void getIntroduceData(){
        DataRequest req = new DataRequest();
        DataResponse res;
        res = HttpRequestUtil.request("/api/student/getStudentIntroduceData",req);
        if(res == null || res.getCode() != 0)
            MessageDialog.showDialog("无法获取学生信息");
        Map data =(Map)res.getData();
        Map info = (Map)data.get("info");
        studentId = CommonMethod.getInteger(info,"studentId");
        personId = CommonMethod.getInteger(info,"personId");
        num.setText(CommonMethod.getString(info,"num"));
        name.setText(CommonMethod.getString(info,"name"));
        dept.setText(CommonMethod.getString(info,"dept"));
        major.setText(CommonMethod.getString(info,"major"));
        className.setText(CommonMethod.getString(info,"className"));
        card.setText(CommonMethod.getString(info,"card"));
        gender.setText(CommonMethod.getString(info,"genderName"));
        birthday.setText(CommonMethod.getString(info,"birthday"));
        email.setText(CommonMethod.getString(info,"email"));
        phone.setText(CommonMethod.getString(info,"phone"));
        address.setText(CommonMethod.getString(info,"address"));

        initScoreList((List) data.get("scoreList"));
        setScoreTable();

        markList = (List)data.get("markList");
        ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();
        for(Map m:markList) {
            chartData.add(new PieChart.Data(m.get("title").toString(),Double.parseDouble(m.get("value").toString())));
        }
        pieChart.setData(chartData);  //成绩分类表显示

        feeList = (List)data.get("feeList");
        XYChart.Series<String, Number> seriesFee = new XYChart.Series<>();
        seriesFee.setName("日常消费");
        for(Map m:feeList)
            seriesFee.getData().add(new XYChart.Data<>(m.get("title").toString(),Double.parseDouble(m.get("value").toString())));
        ObservableList<XYChart.Series<String, Number>> barData = FXCollections.observableArrayList();
        barData.add(seriesFee);
        barChart.setData(barData); //消费数据直方图展示
        displayPhoto();

    }
    public void displayPhoto(){
        DataRequest req = new DataRequest();
        req.add("fileName", "photo/" + personId + ".jpg");  //个人照片显示
        byte[] bytes = HttpRequestUtil.requestByteData("/api/base/getFileByteData", req);
        if (bytes != null) {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            Image img = new Image(in);
            photoImageView.setImage(img);
        }

    }

    //按下编辑个人简历按钮时打开编辑页面
    public void onEditIntroButtonClick(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("student-intro-editor.fxml"));
            //以A4纸的宽高比210:297打开页面
            Stage s = WindowsManager.getInstance().openNewWindow(
                    loader, 565, 800, "编辑简历",
                    photoButton.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller, Stage stage) {
                            WindowOpenAction.super.init(controller, stage);
                            ((StudentIntroEditorController)controller).setStage(stage);
                        }
                    }
            );
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开个人简历编辑页面失败");
        }
    }

    //按下查看个人画像的时候，打开个人画像页面
    public void onCheckDraw(){
        try{
            FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("studentDraw/student-draw.fxml"));
            Stage s = WindowsManager.getInstance().openNewWindow(
                    loader, 566, 800, "个人画像",
                    photoButton.getScene().getWindow(), Modality.WINDOW_MODAL,
                    new WindowOpenAction() {
                        @Override
                        public void init(Object controller) {
                            WindowOpenAction.super.init(controller);
                            StudentDrawController cont = (StudentDrawController) controller;
                            cont.init(StudentIntroduceController.this);
                        }
                    }
            );
            //禁止修改窗口大小
            s.setResizable(false);
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("打开个人画像页面失败! ");
        }
    }


    /**
     * 点击保存按钮 执行onSubmitButtonClick 调用doSave 实现个人简历保存
     */
//    @FXML
//    public void onSubmitButtonClick(){
//        doSave();
//    }

    /**
     * 显示生成的个人简历的PDF， 可以直接将PDF数据存入本地文件参见StudentController 中的doExpert 方法中的本地文件保存
     * 后台修改完善扩展PDF内容的生成方法，可以按照HTML语法生成PDF要展示的数据内容
     */
//    @FXML
//    public void onIntroduceDownloadClick(){
//        DataRequest req = new DataRequest();
//        req.add("studentId",studentId);
//        byte[] bytes = HttpRequestUtil.requestByteData("/api/student/getStudentIntroducePdf", req);
//        if (bytes != null) {
//            try {
//                MessageDialog.pdfViewerDialog(bytes);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     *  点击图片位置，可以重现上传图片，可在本地目录选择要上传的张片进行上传
     */
    @FXML
    public void onPhotoButtonClick(){
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("图片上传");
//        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JPG 文件", "*.jpg"));
        File file = fileDialog.showOpenDialog(null);
        if(file == null)
            return;
        DataResponse res =HttpRequestUtil.uploadFile("/api/base/uploadPhoto",file.getPath(),personId + ".jpg");
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
            displayPhoto();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }
    /**
     * 保存个人简介数据到数据库里
     */

//    public void doSave(){
//        String introduce = introduceHtml.getHtmlText();
//        DataRequest req = new DataRequest();
//        req.add("studentId",studentId);
//        req.add("introduce",introduce);
//        DataResponse res = HttpRequestUtil.request("/api/student/saveStudentIntroduce", req);
//        if(res.getCode() == 0) {
//            MessageDialog.showDialog("提交成功！");
//        }else {
//            MessageDialog.showDialog(res.getMsg());
//        }
//    }

    /**
     * 数据导入示例，点击编辑菜单中的导入菜单执行该方法， doImport 重写了 ToolController 中的doImport
     * 该方法从本地选择Excl文件，数据上传到后台，后台从Excl格式的数据流中解析出日期和金额添加更新学生的消费记录
     */
    public void doImport(){
        FileChooser fileDialog = new FileChooser();
        fileDialog.setTitle("前选择消费数据表");
        fileDialog.setInitialDirectory(new File("C:/"));
        fileDialog.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLSX 文件", "*.xlsx"));
        File file = fileDialog.showOpenDialog(null);
        String paras = "studentId="+studentId;
        DataResponse res =HttpRequestUtil.importData("/api/student/importFeeData",file.getPath(),paras);
        if(res.getCode() == 0) {
            MessageDialog.showDialog("上传成功！");
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    @Override
    public void doExport(){
        //选择保存路径
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("请选择保存位置");
        //文件路径
        File file = chooser.showDialog(scoreTable.getScene().getWindow());

        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        byte[] pdfBytes = HttpRequestUtil.requestByteData("/api/student/getIntroducePdf", req);
        if(pdfBytes == null){
            MessageDialog.showDialog("获取个人画像失败! ");
            return;
        }
        try{
            String fileName = name.getText() + " " + num.getText() + "个人简介" + ".pdf";
            CommonMethod.saveFile(file.getPath(), fileName, pdfBytes);
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("保存个人简介失败! ");
        }

    }

}