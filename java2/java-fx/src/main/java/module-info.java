module com.teach.javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires java.logging;
    requires com.google.gson;
    requires java.net.http;
    requires spring.security.crypto;
    requires javafx.media;
    requires MaterialFX;

    opens com.teach.javafx to javafx.fxml;
    opens com.teach.javafx.request to com.google.gson, javafx.fxml;
    opens com.teach.javafx.controller.base to com.google.gson, javafx.fxml;
    opens com.teach.javafx.controller to com.google.gson, javafx.fxml;
    opens org.fatmansoft.teach.models to javafx.base,com.google.gson;
    opens org.fatmansoft.teach.util to com.google.gson, javafx.fxml;
    opens org.fatmansoft.teach.payload.request to com.google.gson, javafx.fxml;
    opens org.fatmansoft.teach.payload.response to com.google.gson, javafx.fxml;

    exports com.teach.javafx;
    exports com.teach.javafx.controller;
    exports com.teach.javafx.controller.base;
    exports com.teach.javafx.request;
    exports org.fatmansoft.teach.util;
    exports com.teach.javafx.customWidget;
    exports com.teach.javafx.controller.studentScore;
    exports com.teach.javafx.controller.adminCoursePanel;
    opens com.teach.javafx.models to com.google.gson, javafx.base;
    exports com.teach.javafx.controller.honor;
    opens com.teach.javafx.controller.honor to com.google.gson, javafx.fxml;
    exports com.teach.javafx.controller.courseSelection;
    opens com.teach.javafx.controller.courseSelection to com.google.gson, javafx.fxml;

}