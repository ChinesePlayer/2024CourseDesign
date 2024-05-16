package com.teach.javafx.models;

import java.io.Serializable;

public class Shortcut implements Serializable {
    private String name;
    private String fxml;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFxml() {
        return fxml;
    }

    public void setFxml(String fxml) {
        this.fxml = fxml;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Shortcut)){
            return false;
        }
        if(o == this){
            return true;
        }
        if(((Shortcut) o).name.equals(this.name) && ((Shortcut) o).fxml.equals(this.fxml)){
            return true;
        }
        return false;
    }
}
