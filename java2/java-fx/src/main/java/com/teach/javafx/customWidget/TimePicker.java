package com.teach.javafx.customWidget;

import io.github.palexdev.materialfx.controls.models.spinner.IntegerSpinnerModel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

import java.time.LocalTime;

//用于选择时间
public class TimePicker extends HBox {
    private Spinner<Integer> hour;
    private Spinner<Integer> minute;
    private Spinner<Integer> second;

    //初始化块，初始化时分秒选择器并为其设置范围限制
    {
        SpinnerValueFactory<Integer> factory= new SpinnerValueFactory.IntegerSpinnerValueFactory(0,23,0);
        hour = new Spinner<>();
        hour.setValueFactory(factory);

        factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        minute = new Spinner<>();
        minute.setValueFactory(factory);

        factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        second = new Spinner<>();
        second.setValueFactory(factory);

        hour.setPrefWidth(60);
        minute.setPrefWidth(60);
        second.setPrefWidth(60);

        hour.setMaxWidth(60);
        minute.setMaxWidth(60);
        second.setMaxWidth(60);

        //将控件加入父组件中
        getChildren().addAll(hour,new Label(":"),minute,new Label(":"),second);
        setAlignment(Pos.CENTER);
    }

    public TimePicker(){

    }

    public Integer getHour(){
        return hour.getValue();
    }

    public Integer getMinute(){
        return minute.getValue();
    }

    public Integer getSecond(){
        return second.getValue();
    }

    //时间设置器，限制范围
    //若超过最大范围，则设为最大值
    //若小于最小范围，则设为最小值
    public void setHour(Integer hour){
        if(hour > 23){
            hour = 23;
        }
        else if(hour < 0){
            hour = 0;
        }
        this.hour.getValueFactory().setValue(hour);
    }

    public void setMinute(Integer minute){
        if(minute > 59){
            minute = 59;
        }
        else if(minute < 0){
            minute = 0;
        }
        this.minute.getValueFactory().setValue(minute);
    }

    public void setSecond(Integer second){
        if(second > 59){
            second = 59;
        }
        else if(second < 0){
            second = 0;
        }
        this.second.getValueFactory().setValue(second);
    }

    public void setTime(LocalTime time){
        this.hour.getValueFactory().setValue(time.getHour());
        this.minute.getValueFactory().setValue(time.getMinute());
        this.second.getValueFactory().setValue(time.getSecond());
    }

    //转化为标准字符串
    //格式为: HH:mm:ss
    @Override
    public String toString(){
        return hour.getValue() + ":" + minute.getValue() + ":" + second.getValue();
    }

    public LocalTime getLocalTime(){
        LocalTime localTime = LocalTime.of(getHour(), getMinute(), getSecond());
        return localTime;
    }
}
