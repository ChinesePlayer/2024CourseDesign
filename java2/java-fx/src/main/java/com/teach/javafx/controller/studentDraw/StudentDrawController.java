package com.teach.javafx.controller.studentDraw;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.teach.javafx.controller.StudentIntroduceController;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.controller.honor.HonorCheckController;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.CommonMethod;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class StudentDrawController {
    @FXML
    public Label name;
    @FXML
    public Label birthDay;
    @FXML
    public Label phone;
    @FXML
    public Label email;
    @FXML
    public Label major;
    @FXML
    public Label college;
    @FXML
    public Label course1;
    @FXML
    public Label course2;
    @FXML
    public Label course3;
    @FXML
    public Label gpa;
    @FXML
    public Label honor1;
    @FXML
    public Label honor2;
    @FXML
    public LineChart<String, Number> costChart;
    @FXML
    public PieChart scorePie;
    @FXML
    public ImageView avatar;
    @FXML
    public FlowPane actions;
    private StudentIntroduceController studentIntroduceController;

    public List<Honor> honorList = new ArrayList<>();
    public List<Fee> feeList = new ArrayList<>();
    public List<Map> markList = new ArrayList<>();

    public void init(StudentIntroduceController sc){
        //填充学生信息
        this.studentIntroduceController = sc;
        //从后端获取荣誉列表
        getHonors();
        //填充基本个人信息
        name.setText(studentIntroduceController.name.getText());
        birthDay.setText(studentIntroduceController.birthday.getText());
        phone.setText(studentIntroduceController.phone.getText());
        email.setText(studentIntroduceController.email.getText());
        major.setText(studentIntroduceController.major.getText());
        college.setText(studentIntroduceController.dept.getText());
        //学生照片
        avatar.setImage(studentIntroduceController.photoImageView.getImage());
        //课程成绩
        List<Score> topThree = getTopThree(studentIntroduceController.scoreList);
        Score s;
        if(topThree.size()>0){
            s = topThree.get(0);
            course1.setText(s.getCourseName()+"  "+s.getMark()+"分");
        }
        if(topThree.size()>1){
            s = topThree.get(1);
            course2.setText(s.getCourseName()+"  "+s.getMark()+"分");
        }
        if(topThree.size()>2){
            s = topThree.get(2);
            course3.setText(s.getCourseName()+"  "+s.getMark()+"分");
        }
        //学生绩点
        double GPA = calcGpa(studentIntroduceController.scoreList);
        if(GPA > 0){
            gpa.setText(GPA+"");
        }
        else{
            gpa.setText("暂无绩点");
        }
        //个人荣誉
        if(honorList.size()>0){
            honor1.setText(honorList.get(0).getHonorContent());
        }
        if(honorList.size()>1){
            honor2.setText(honorList.get(1).getHonorContent());
        }
        //消费折线图
        setCostLineChart();
        //成绩饼状图
        setMarkPie();
    }

    //获取前三的成绩
    public List<Score> getTopThree(List<Score> scoreList){
        List<Score> res = new ArrayList<>();
        scoreList.sort((o1, o2) -> o2.getMark().compareTo(o1.getMark()));
        for(int i = 0; i < 3 && i < scoreList.size(); i++){
            res.add(scoreList.get(i));
        }
        return res;
    }

    public double calcGpa(List<Score> scoreList){
        int non0Score=0;
        double sum=0;
        for(Score s : scoreList){
            if(s.getStatus() != 0){
                non0Score++;
            }
            if(s.getStatus() == 1){
                sum+=s.getMark() / 10 - 5;
            }
        }
        if(non0Score != 0){
            return  sum /non0Score;
        }
        else{
            return -1;
        }
    }

    //从后端获取个人荣誉信息
    public void getHonors(){
        DataRequest req = new DataRequest();
        DataResponse res = HttpRequestUtil.request("/api/honor/getHonors", req);
        assert res != null;
        if(res.getCode() == 0){
            //先清空荣誉列表
            honorList.clear();
            List<Map> rawData = (ArrayList<Map>) res.getData();
            for(Map m : rawData){
                Honor h = new Honor(m);
                honorList.add(h);
            }
        }
    }

    //填充消费折线图的数据
    public void setCostLineChart(){
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        costChart.getData().clear();
        for(Map m : studentIntroduceController.feeList){
            Fee fee = new Fee(m);
            feeList.add(fee);
            series.getData().add(new XYChart.Data<>(fee.getDateTime().format(formatter), fee.getValue()));
        }
        costChart.setLegendVisible(false);
        costChart.setData(FXCollections.observableArrayList(series));
    }

    //填充成绩饼状图数据
    public void setMarkPie(){
        markList = studentIntroduceController.markList;
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for(Map m : markList){
            pieData.add(new PieChart.Data(CommonMethod.getString(m, "title"), CommonMethod.getInteger(m,"value")));
        }
        scorePie.getData().clear();
        scorePie.setData(pieData);
        scorePie.setLegendVisible(false);
    }

    //保存图片
    public void saveAsImage(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择保存路径");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png图片", "*.png"));
        File file = chooser.showSaveDialog(actions.getScene().getWindow());
        //隐藏按钮
        actions.setVisible(false);
        //截图
        SnapshotParameters parameters = new SnapshotParameters();
        WritableImage image = actions.getScene().getRoot().snapshot(parameters, null);
        try{
            boolean isSuccess = ImageIO.write(SwingFXUtils.fromFXImage(image,null),"png", file);
            if(isSuccess){
                System.out.println("图片已保存至: " + file.getAbsolutePath());
                MessageDialog.showDialog("图片已保存至: " + file.getAbsolutePath());
            }
            else{
                MessageDialog.showDialog("保存图片失败");
            }
        }
        catch (IOException e){
            e.printStackTrace();
            MessageDialog.showDialog("保存图片失败");
        }
        finally {
            //重新将按钮设为可见
            actions.setVisible(true);
        }
    }

    //保存PDF
    public void saveAsPdf(){
        FileChooser chooser = new FileChooser();
        chooser.setTitle("选择保存路径");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("pdf文档", "*.pdf"));
        File file = chooser.showSaveDialog(actions.getScene().getWindow());
        Document doc = new Document();
        try{
            PdfWriter.getInstance(doc, new FileOutputStream(file));
            //隐藏按钮
            actions.setVisible(false);

            WritableImage snap = actions.getScene().getRoot().snapshot(new SnapshotParameters(), null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(SwingFXUtils.fromFXImage(snap,null),"png",outputStream);
            outputStream.flush();
            byte[] bytes = outputStream.toByteArray();
            outputStream.close();
            Image image = Image.getInstance(bytes);
            image.setAlignment(Image.ALIGN_CENTER);
            doc.open();
            doc.add(image);

            MessageDialog.showDialog("PDF已保存至: " + file.getAbsolutePath());
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            //重新将按钮设为可见
            actions.setVisible(true);
            doc.close();
        }
    }
}
