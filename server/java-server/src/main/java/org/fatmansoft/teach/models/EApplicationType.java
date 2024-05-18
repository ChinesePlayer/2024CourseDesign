package org.fatmansoft.teach.models;

public enum EApplicationType {
    //暂时离校
    APP_TEMP,
    //长期离校
    APP_LONG,
    //永久离校
    APP_FOREVER;

    public static EApplicationType fromString(String str){
        switch (str){
            case "APP_TEMP":{
                return APP_TEMP;
            }
            case "APP_LONG":{
                return APP_LONG;
            }
            case "APP_FOREVER":{
                return APP_FOREVER;
            }
            default:{
                throw new IllegalArgumentException("无法识别该请假类型");
            }
        }
    }
}
