package com.teach.javafx.request;

import afester.javafx.svg.SvgLoader;
import javafx.scene.Group;
import javafx.scene.shape.SVGPath;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

public class SvgImage {
    private String svgPath;

    public SvgImage(){

    }

    public Group toGroup(){
        SvgLoader loader = new SvgLoader();
        if(svgPath == null){
            return null;
        }
        InputStream svgStream = new ByteArrayInputStream(svgPath.getBytes());
        Group res = loader.loadSvg(svgStream);
        return res;
    }

    public SvgImage(String s){
        this.svgPath = s;
    }
    public String getSvgPath() {
        return svgPath;
    }



    public void setSvgPath(String svgPath) {
        this.svgPath = svgPath;
    }
}
