package com.teach.javafx.controller.statistic;

import com.teach.javafx.AppStore;
import com.teach.javafx.request.HttpRequestUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import org.fatmansoft.teach.models.Fee;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class FeeStatisticController {
    public final Map<String, Integer> rangeMap = new HashMap<>();
    @FXML
    public LineChart<String, Number> lineChart;
    @FXML
    public ComboBox<String> rangeCombo;
    public List<Fee> feeList = new ArrayList<>();

    {
        //选项与天数的映射关系
        rangeMap.put("一年内", 12 * 30);
        rangeMap.put("半年内", 6 * 30);
        rangeMap.put("三个月内", 3 * 30);
        rangeMap.put("一个月内", 30);
        rangeMap.put("两周内", 2 * 7);
        rangeMap.put("一周内", 7);
        rangeMap.put("全部", Integer.MAX_VALUE);
    }

    @FXML
    public void initialize(){
        ObservableList<String> obS = FXCollections.observableArrayList();
        obS.addAll("全部", "一年内", "半年内","三个月内","一个月内","两周内","一周内");
        rangeCombo.setItems(obS);
        rangeCombo.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String selectedOption = rangeCombo.getSelectionModel().getSelectedItem();
                int range = rangeMap.get(selectedOption);
                List<Fee> rangedList = findWithinFee(range);
                System.out.println(rangedList);
                setDataView(rangedList, selectedOption);
            }
        });
        getFeeList();
        setDataView(feeList, "全部");
    }

    public void getFeeList(){
        feeList.clear();
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        DataResponse res = HttpRequestUtil.request("/api/student/getStudentFeeList", req);
        assert res != null;
        if(res.getCode() == 0){
            List<Map> rawData = (ArrayList<Map>)res.getData();
            for(Map m : rawData){
                Fee f = new Fee(m);
                feeList.add(f);
            }
            //对消费按照日期先后排序
            sortFee(feeList);
        }
    }

    public void sortFee(List<Fee> fList){
        if(fList.isEmpty()){
            return;
        }
        fList.sort(Comparator.comparing(Fee::getDateTime));
    }

    //设置折线图显示的数据
    public void setDataView(List<Fee> rangedFee, String rangeName){
        if (rangedFee == null){
            rangedFee = new ArrayList<>();
        }
        lineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        series.setName(rangeName + " 消费数据");
        for(Fee f : rangedFee){
            series.getData().add(new XYChart.Data<>(f.getDateTime().format(formatter), f.getValue()));
        }
        lineChart.setData(FXCollections.observableArrayList(series));
    }

    //找到特定时间范围内的日期，并且排好序
    public List<Fee> findWithinFee(int within){
        if(within < 0){
            return new ArrayList<>();
        }
        List<Fee> fList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for(Fee f : feeList){
            //计算两个日期之间的天数差
            long dayDiff = ChronoUnit.DAYS.between(f.getDateTime(), now);
            if(dayDiff <= within){
                //在范围内，添加到数组中
                fList.add(f);
            }
        }
        sortFee(fList);
        return fList;
    }

}
