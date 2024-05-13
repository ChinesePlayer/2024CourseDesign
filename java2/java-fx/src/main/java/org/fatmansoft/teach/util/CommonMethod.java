package org.fatmansoft.teach.util;


import com.teach.javafx.AppStore;
import com.teach.javafx.controller.base.MessageDialog;
import com.teach.javafx.request.HttpRequestUtil;
import com.teach.javafx.request.OptionItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
/**
 * CommonMethod 公共处理方法实例类
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonMethod {
    public static final String DISPLAY_DATE_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    //使用正则表达式来判断邮箱是否合法
    public static boolean isValidEmail(String email){
        //正则表达式的模式字符串
        //注意：由于正则表达式的元字符有反斜杠，而反斜杠在Java中也有转义作用，所以当使用含有
        //反斜杠的正则元字符时，需要写两个反斜杠
        String patternString = "^[a-zA-Z0-9_]+@[a-zA-Z0-9]+(\\.[a-zA-Z]{2,})+$";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
    public static String[] getStrings(Map data,String key){
        Object obj = data.get(key);
        if(obj == null)
            return new String[]{};
        if(obj instanceof String[])
            return (String[])obj;
        return new String[]{};
    }

    public static String getString(Map data,String key){
        if(data == null)
            return "";
        Object obj = data.get(key);
        if(obj == null)
            return "";
        if(obj instanceof String)
            return (String)obj;
        return obj.toString();
    }

    public static LocalDateTime getDateTime(Map data, String key){
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof String){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            return LocalDateTime.parse((String)obj, formatter);
        }
        return null;
    }
    public static Boolean getBoolean(Map data,String key){
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return false;
        if(obj instanceof Boolean)
            return (Boolean)obj;
        if("true".equals(obj.toString()))
            return true;
        else
            return false;
    }
    public static List getList(Map data, String key){
        if(data == null)
            return new ArrayList();
        Object obj = data.get(key);
        if(obj == null)
            return new ArrayList();
        if(obj instanceof List)
            return (List)obj;
        else
            return new ArrayList();
    }
    public static Map getMap(Map data,String key){
        if(data == null)
            return new HashMap();
        Object obj = data.get(key);
        if(obj == null)
            return new HashMap();
        if(obj instanceof Map)
            return (Map)obj;
        else
            return new HashMap();
    }
    public static Integer getIntegerFromObject(Object obj) {
        if(obj == null)
            return null;
        if(obj instanceof Integer)
            return (Integer)obj;
        String str = obj.toString();
        try {
            return (int)Double.parseDouble(str);
        }catch(Exception e) {
            return null;
        }
    }

    public static Integer getInteger(Map data,String key) {
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof Integer)
            return (Integer)obj;
        String str = obj.toString();
        try {
            return (int)Double.parseDouble(str);
        }catch(Exception e) {
            return null;
        }
    }
    public static Integer getInteger0(Map data,String key) {
        if(data == null)
            return 0;
        Object obj = data.get(key);
        if(obj == null)
            return 0;
        if(obj instanceof Integer)
            return (Integer)obj;
        String str = obj.toString();
        try {
            return (int)Double.parseDouble(str);
        }catch(Exception e) {
            return 0;
        }
    }

    public static Long getLong(Map data,String key) {
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof Long)
            return (Long) obj;
        String str = obj.toString();
        try {
            return (long)Double.parseDouble(str);
        }catch(Exception e) {
            return null;
        }
    }

    public static Double getDouble(Map data,String key) {
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof Double)
            return (Double)obj;
        String str = obj.toString();
        try {
            return Double.parseDouble(str);
        }catch(Exception e) {
            return null;
        }
    }
    public static Double getDouble0(Map data,String key) {
        Double d0 = 0d;
        Object obj = data.get(key);
        if(obj == null)
            return d0;
        if(obj instanceof Double)
            return (Double)obj;
        String str = obj.toString();
        try {
            return d0;
        }catch(Exception e) {
            return d0;
        }
    }
    public static Date getDate(Map data, String key) {
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof Date)
            return (Date)obj;
        String str = obj.toString();
        return DateTimeTool.formatDateTime(str,"yyyy-MM-dd");
    }
    public static Date getTime(Map data,String key) {
        if(data == null)
            return null;
        Object obj = data.get(key);
        if(obj == null)
            return null;
        if(obj instanceof Date)
            return (Date)obj;
        String str = obj.toString();
        return DateTimeTool.formatDateTime(str,"yyyy-MM-dd HH:mm:ss");
    }

    public static List<OptionItem> changeMapListToOptionItemList(List<Map> mapList) {
        List<OptionItem> itemList = new ArrayList();
        for(Map m:mapList){
            itemList.add(new OptionItem((Integer)m.get("id"),(String)m.get("value"),(String)m.get("label")));
        }
        return itemList;
    }
    public static int getOptionItemIndexById(List<OptionItem>itemList, Integer id){
        if(id == null)
            return -1;
        for(int i = 0; i < itemList.size();i++) {
            if(itemList.get(i).getId().equals(id))
                return i;
        }
        return -1;
    }
    public static int getOptionItemIndexByValue(List<OptionItem>itemList, String value){
        if(value == null || value.length() == 0)
            return -1;
        for(int i = 0; i < itemList.size();i++) {
            if(itemList.get(i).getValue().equals(value))
                return i;
        }
        return -1;
    }
    public static String getOptionItemTitleByValue(List<OptionItem>itemList, String value){
        if(value == null || value.length() == 0)
            return "";
        for(int i = 0; i < itemList.size();i++) {
            if(itemList.get(i).getValue().equals(value))
                return itemList.get(i).getTitle();
        }
        return "";
    }

    public static int getOptionItemIndexByTitle(List<OptionItem>itemList, String title){
        if(title == null || title.length() == 0)
            return -1;
        for(int i = 0; i < itemList.size();i++) {
            if(itemList.get(i).getTitle().equals(title))
                return i;
        }
        return -1;
    }

    public static String addThingies(String s) {
        return "'" + mysql_real_escape_string(s) + "'";
    }

    //SQLi protection
    public static String mysql_real_escape_string(String str) {
        if (str == null) {
            return null;
        }

        if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]", "").length() < 1) {
            return str;
        }

        String clean_string = str;
        clean_string = clean_string.replaceAll("\\\\", "\\\\\\\\");
        clean_string = clean_string.replaceAll("\\n", "\\\\n");
        clean_string = clean_string.replaceAll("\\r", "\\\\r");
        clean_string = clean_string.replaceAll("\\t", "\\\\t");
        clean_string = clean_string.replaceAll("\\00", "\\\\0");
        clean_string = clean_string.replaceAll("'", "\\\\'");
        clean_string = clean_string.replaceAll("\\\"", "\\\\\"");

        if (clean_string.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/?\\\\\"' ]", "").length() < 1) {
            return clean_string;
        }
        return str;
    }
    public static List<OptionItem> changeMapToOptionItemList(List<Map> mList) {
        List<OptionItem> iList = new ArrayList();
        if(mList == null)
            return iList;
        for( Map m : mList) {
            iList.add(new OptionItem(m));
        }
        return iList;
    }

    //通过指定某个元素，获取该元素所在的TableView行的数据
    //level: 指定父亲级别
    public static Object getRowValue(ActionEvent event, int level, TableView tableView){
        Node n = (Node)event.getTarget();
        for(int i = 0; i < level; i++){
            n = n.getParent();
        }
        TableCell<?, ?> cell = (TableCell<?, ?>) n;
        int rowIndex = cell.getIndex();
        return tableView.getItems().get(rowIndex);
    }

    public static Integer getStatusInt(String str){
        return switch (str) {
            case "修读中" -> 0;
            case "已及格" -> 1;
            case "不及格" -> 2;
            default -> -1;
        };
    }

    public static String getStatusStr(Integer status){
        return switch (status) {
            case 0 -> "修读中";
            case 1 -> "已及格";
            case 2 -> "不及格";
            default -> "----";
        };
    }

    //限制某个TextField只能输入整数
    public static void limitToNumber(TextField tf){
        tf.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                tf.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    //检查某门课程该学生是否已经通过
    public static boolean wasPassed(Integer courseId){
        DataRequest req = new DataRequest();
        req.add("studentId", AppStore.getJwt().getRoleId());
        req.add("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/getWasPassed", req);
        assert res != null;
        if(res.getCode() == 0){
            Boolean wasPassed = (Boolean) res.getData();
            return wasPassed;
        }
        else{
            MessageDialog.showDialog("无法获取修读信息! ");
            throw new RuntimeException("无法获取修读信息");
        }
    }

    //获取课程状态
    public static Integer getCourseStatus(Integer courseId){
        DataRequest req = new DataRequest();
        req.add("courseId", courseId);
        DataResponse res = HttpRequestUtil.request("/api/course/getCourseStatus",req);
        assert res != null;
        if(res.getCode() == 0){
            Integer status = (Integer) res.getData();
            return status;
        }
        throw new RuntimeException("无法获取课程状态: " + res.getMsg());
    }

    //将Map中的元素填入指定GridPane中
    //startRow为从第几行开始，startColumn为从第几列开始
    public static void addItemToGridPane(List<List> data, GridPane gridPane, int startRow, int startColumn){
        final int[] row = {startRow};
        data.forEach(new Consumer<List>() {
            @Override
            public void accept(List list) {
                final int[] column = {startColumn};
                list.forEach(o -> {
                    gridPane.add((Node) o,column[0] , row[0]);
                    column[0]++;
                });
                row[0]++;
            }
        });
    }

    public static String getDateString(LocalDateTime dateTime, String pattern){
        if(pattern == null){
            pattern = DISPLAY_DATE_FORMAT;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    //删除指定的GridPane中某个矩形区域的所有元素
    public static void deleteRectFromGridPane(GridPane pane, int sRow, int sColumn, int eRow, int eColumn){
        pane.getChildren().removeIf(node -> {
            Integer rowIndex = GridPane.getRowIndex(node);
            Integer columnIndex = GridPane.getColumnIndex(node);
            return rowIndex != null && columnIndex != null && sColumn <= columnIndex && columnIndex <= eColumn && sRow <= rowIndex && rowIndex <= eRow;
        });
    }

    //序列化Map
    public static byte[] mapToByteArray(Map map) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(map);
        oos.close();
        return baos.toByteArray();
    }

    //将文件的字节大小转换为合适的单位大小
    public static String formatFileSize(long sizeInBytes) {
        if (sizeInBytes <= 0) return "0 B";

        final String[] units = {"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(sizeInBytes) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(sizeInBytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    //生成唯一文件名
    public static String generateUniqueFileName(String originalFileName, String path) {
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
            originalFileName = originalFileName.substring(0, dotIndex);
        }
        String fileName = originalFileName;
        int count = 0;
        while (Files.exists(Paths.get( path + "/" + fileName + extension))) {
            count++;
            fileName = originalFileName + "(" + count + ")";
        }
        return fileName + extension;
    }
}
