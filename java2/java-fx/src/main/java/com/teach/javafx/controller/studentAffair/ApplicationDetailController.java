package com.teach.javafx.controller.studentAffair;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.fatmansoft.teach.models.Application;
import org.fatmansoft.teach.util.CommonMethod;

public class ApplicationDetailController {
    @FXML
    public TextArea reasonField;
    @FXML
    public Label typeLabel;
    @FXML
    public Label returnLabel;
    @FXML
    public Label leaveLabel;

    private Application application;

    @FXML
    public void initialize(){
        reasonField.setEditable(false);
    }

    public void init(Application a){
        this.application = a;
        setDateView(a);
    }

    public void setDateView(Application a){
        if(a == null){
            return;
        }
        reasonField.setText(a.getReason());
        typeLabel.setText(Application.getDisplayString(a.getApplicationType()));
        leaveLabel.setText(CommonMethod.getDateString(a.getLeaveDate(),CommonMethod.DISPLAY_DATE_FORMAT));
        returnLabel.setText(CommonMethod.getDateString(a.getReturnDate(),CommonMethod.DISPLAY_DATE_FORMAT));
    }
}
