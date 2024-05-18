package com.teach.javafx.controller.adminCoursePanel;

import com.teach.javafx.controller.CourseController;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.customWidget.TimeSelector;
import com.teach.javafx.request.HttpRequestUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXSpinner;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.controls.models.spinner.DoubleSpinnerModel;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Course;
import org.fatmansoft.teach.models.CourseLocation;
import org.fatmansoft.teach.models.CourseTime;
import org.fatmansoft.teach.models.Teacher;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.util.*;

public class EditCourseController {
    @FXML
    public MFXTextField name;
    @FXML
    public MFXSpinner<Double> credit;
    @FXML
    public VBox timeGroup;
    @FXML
    public ComboBox<Teacher> teacher;
    @FXML
    public ComboBox<CourseLocation> loc;
    @FXML
    public ComboBox<Course> preCourse;
    @FXML
    public MFXButton save;
    @FXML
    public MFXButton cancel;
    @FXML
    public MFXTextField num;
    @FXML
    public MFXButton delete;
    private Course course;

    private CourseController courseController;
    private List<Teacher> teacherList = new ArrayList<>();
    private List<CourseLocation> locationList = new ArrayList<>();
    private List<Course> preCourseList = new ArrayList<>();

    private Teacher emptyTeacher = null;
    private Course emptyCourse = null;
    private CourseLocation emptyLocation = null;



    @FXML
    public void initialize(){
        //设置学分增长步长为0.5
        DoubleSpinnerModel model = new DoubleSpinnerModel();
        model.setIncrement(0.5);
        model.setMax(9.0);
        model.setMin(1.0);
        credit.setSpinnerModel(model);
    }

    //  从后端获取所有老师数据
    public void getTeacherList(){
        DataRequest req = new DataRequest();
        List<Teacher> res = HttpRequestUtil.requestDataList("/api/teacher/getTeacherList", req);
        if(res != null && !res.isEmpty()){
            teacherList.clear();
            //在第一位加入一个空老师
            emptyTeacher = new Teacher();
            emptyTeacher.setTeacherId(0);
            teacherList.add(emptyTeacher);
            for(Teacher m : res){
                m.setPersonName(m.getPerson().getPersonName());
                teacherList.add(m);
            }
        }
    }

    public void getPreCourseList(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseList", req);
        if(res != null && res.getCode() == 0){
            preCourseList.clear();
            //在第一位加入一个为ID为-1的空课程
            emptyCourse = new Course();
            emptyCourse.setCourseId(0);
            preCourseList.add(emptyCourse);
            List<Map> maps = (ArrayList<Map>) res.getData();
            for(Map m : maps){
                preCourseList.add(new Course(m));
            }
            //从列表中删除该课程本身，因为自己不能是自己的前序课程
            preCourseList.remove(course);
        }
    }

    public void getCourseLocations(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/location/getCourseLocationList", req);
        if(res != null && res.getCode() == 0){
            locationList.clear();
            emptyLocation = new CourseLocation();
            emptyLocation.setId(-1);
            locationList.add(emptyLocation);
            List<Map> maps = (ArrayList<Map>) res.getData();
            for(Map m : maps){
                CourseLocation cl = new CourseLocation(m);
                locationList.add(cl);
            }
        }
        else{
            assert res != null;
            MessageDialog.showDialog(res.getMsg());
        }
    }

    public void initData(Course c, CourseController courseController){
        course = c;
        name.setText(c.getName());
        credit.setValue(c.getCredit());
        num.setText(c.getNum());

        getTeacherList();
        teacher.setItems(FXCollections.observableArrayList(teacherList));
        //选中当前老师
        if(c.getTeacher() == null){
            teacher.setValue(emptyTeacher);
        }
        else{
            teacher.setValue(c.getTeacher());
        }

        getCourseLocations();
        loc.setItems(FXCollections.observableArrayList(locationList));
        //选中当前地点
        if(c.getLocation() == null){
            loc.setValue(emptyLocation);
        }
        else{
            loc.setValue(c.getLocation());
        }

        getPreCourseList();
        preCourse.setItems(FXCollections.observableArrayList(preCourseList));
        //选中当前前序课程
        if(c.getPreCourse() == null){
            preCourse.setValue(emptyCourse);
        }
        else{
            preCourse.setValue(c.getPreCourse());
        }

        if(c.getCourseTimes() != null){
            for(CourseTime ct : c.getCourseTimes()){
                MFXButton button = new MFXButton("删除");
                button.setOnAction(this::onDeleteTimeSelector);
                TimeSelector ts = new TimeSelector(ct.getDay(), ct.getSection(), button);
                timeGroup.getChildren().add(ts);
                timeGroup.setAlignment(Pos.TOP_CENTER);
            }
        }
        //若当前课程为新建课程，则不显示删除按钮
        if(course.getCourseId() == null){
            delete.setVisible(false);
        }
        setCourseController(courseController);
    }


    public CourseController getCourseController() {
        return courseController;
    }

    public void setCourseController(CourseController courseController) {
        this.courseController = courseController;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

//    public Teacher getCurrentTeacher() {
//        return currentTeacher;
//    }
//
//    public void setCurrentTeacher(Teacher currentTeacher) {
//        this.currentTeacher = currentTeacher;
//    }
//
//    public List<Teacher> getTeachers() {
//        return teachers;
//    }
//
//    public void setTeachers(List<Teacher> teachers) {
//        this.teachers = teachers;
//    }

    public void addTimeSelector(){
        MFXButton button = new MFXButton("删除");
        button.setOnAction(this::onDeleteTimeSelector);
        timeGroup.getChildren().add(new TimeSelector(button));
    }

    public void onDeleteTimeSelector(ActionEvent event){
        TimeSelector timeSelector = (TimeSelector) ((MFXButton)event.getTarget()).getParent();
        timeGroup.getChildren().remove(timeSelector);
    }

    public void onCancelButton(){
        Stage stage = (Stage) teacher.getScene().getWindow();
        stage.close();
    }

    public void updateCourseFromForm(){
        course.setName(name.getText());
        course.setCredit(credit.getValue());
        //更新表格中前序课程的显示
        if(preCourse.getValue().isEmptyCourse()){
            course.setPreCourse(null);
        }
        else{
            course.setPreCourse(preCourse.getValue());
        }
        //更新表格中老师的显示
        if(teacher.getValue().isEmptyTeacher()){
            course.setTeacher(null);
        }
        else{
            course.setTeacher(teacher.getValue());
        }
        //更新表格中上课地点的显示
        if(loc.getValue().isEmptyLocation()){
            course.setLocation(null);
        }
        else{
            course.setLocation(loc.getValue());
        }

        course.setNum(num.getText());
        List<CourseTime> cts = new ArrayList<>();
        for(Node n : timeGroup.getChildren()){
            TimeSelector ts = (TimeSelector) n;
            CourseTime ct = new CourseTime(ts.getDay(), ts.getSection());
            cts.add(ct);
        }
        course.setCourseTimes(cts);
    }

    //判断新输入的数据是否合法
    public void checkData(){
        String exceptionInfo = "";
        List<TimeSelector> tss = new ArrayList<>();
        //将节点转移到tss数组中
        for(Node n : timeGroup.getChildren()){
            tss.add((TimeSelector) n);
        }
        //检查上课时间是否有重复
        boolean isSame = false;
        for(int i = 0 ; i < tss.size() ; i++){
            for(int j = 0 ; j < tss.size(); j++){
                if(i == j){
                    continue;
                }
                if(tss.get(i).equals(tss.get(j))){
                    isSame = true;
                }
            }
        }
        //生成异常信息
        if(isSame){
            exceptionInfo += "上课时间重复! \n";
        }
        if(name.getText().isEmpty()){
            exceptionInfo += "课程名不能为空! \n";
        }
        if(num.getText().isEmpty()){
            exceptionInfo += "课序号不能为空! \n";
        }
        if(!exceptionInfo.isEmpty()){
            throw new RuntimeException(exceptionInfo);
        }

    }

    public void onSaveButton(){
        try {
            checkData();
        }
        catch (RuntimeException e){
            e.printStackTrace();
            MessageDialog.showDialog(e.getMessage());
            return;
        }
        DataRequest req = new DataRequest();
        req.add("courseId", course.getCourseId());
        //注意此处的名字为新名字
        req.add("courseName", name.getText());
        req.add("num", num.getText());
        req.add("locationId", loc.getValue().getId());
        req.add("teacherId", teacher.getValue().getTeacherId());
        req.add("credit", credit.getValue());
        //此处preCourseId可能小于0，这样后端在查找数据库时就找不到数据，就可以被设为null
        req.add("preCourseId", preCourse.getValue().getCourseId());
        List<Map> timeMapList = new ArrayList<>();
        for(Node n : timeGroup.getChildren()){
            TimeSelector ts = (TimeSelector) n;
            Map m = new HashMap<>();
            m.put("day", ts.getDay());
            m.put("section", ts.getSection());
            timeMapList.add(m);
        }
        req.add("times", timeMapList);
        DataResponse res = HttpRequestUtil.request("/api/course/courseSave", req);
        if(res != null && res.getCode() == 0){
            Map returnData = (Map) res.getData();
            Integer courseId = Integer.parseInt((String) returnData.get("courseId"));
            //为该课程设置从后端返还的ID
            course.setCourseId(courseId);
            updateCourseFromForm();
            courseController.onHasSavedCourse(course);
            System.out.println("修改的课程ID: " + courseId);
            //关闭窗口
            onCancelButton();
        }
        else {
            MessageDialog.showDialog(res.getMsg());
        }
    }

    //根据删除按钮的显示规则，新建的课程不会执行以下代码
    //只有在编辑已有课程的时候才可能会执行到以下代码
    public void onDeleteButton(ActionEvent event) {
        int ret = MessageDialog.choiceDialog("确定要删除 " + course.getName() + " 吗? ");
        if(ret != MessageDialog.CHOICE_YES){
            return;
        }
        DataRequest req = new DataRequest();
        req.add("courseId", course.getCourseId());
        DataResponse res = HttpRequestUtil.request("/api/course/courseDelete", req);
        if(res != null && res.getCode() == 0){
            MessageDialog.showDialog("课程 " + course.getName() + " 删除成功");
            courseController.onHasDeleteCourse(course);
            ((Stage)teacher.getScene().getWindow()).close();
        }
        else{
            MessageDialog.showDialog(res.getMsg());
        }
    }
}
