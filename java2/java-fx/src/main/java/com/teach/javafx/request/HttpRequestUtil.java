package com.teach.javafx.request;

import com.teach.javafx.AppStore;
import com.google.gson.Gson;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.util.JsonConvertUtil;
import org.fatmansoft.teach.util.CommonMethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * HttpRequestUtil 后台请求实例程序，主要实践向后台发送请求的方法
 *  static boolean isLocal 业务处理程序实现方式 false java-server实现 前端程序通过下面的方法把数据发送后台程序，后台返回前端需要的数据，true 本地方式 业务处理 在SQLiteJDBC 实现
 *  String serverUrl = "http://localhost:9090" 后台服务的机器地址和端口号
 */
public class HttpRequestUtil {
    public static boolean isLocal = false;
    private static Gson gson = new Gson();
    private static HttpClient client = HttpClient.newHttpClient();
    public static String serverUrl = "http://localhost:9090";
//    public static String serverUrl = "http://202.194.14.120:9090";

    /**
     *  应用关闭是需要做关闭处理
     */
    public static void close(){
        if(isLocal)
            SQLiteJDBC.getInstance().close();
    }

    /**
     * String login(LoginRequest request)  用户登录请求实现
     * @param request  username 登录账号 password 登录密码
     * @return  返回null 登录成功 AppStore注册登录账号信息 非空，登录错误信息
     */

    public static String login(LoginRequest request){
        if(isLocal) {
            return SQLiteJDBC.getInstance().login(request.getUsername(),request.getPassword());
        }else {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/auth/login"))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .build();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("response.statusCode===="+response.statusCode());
                if (response.statusCode() == 200) {
                    JwtResponse jwt = gson.fromJson(response.body(), JwtResponse.class);
                    AppStore.setJwt(jwt);
                    return null;
                } else if (response.statusCode() == 401) {
                    return "用户名或密码不存在！";
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "登录失败";
        }
    }

    /**
     * DataResponse request(String url,DataRequest request) 一般数据请求业务的实现
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return DataResponse 返回后台返回数据
     */
    public static DataResponse request(String url, DataRequest request){
        if(isLocal) {
            int index = url.lastIndexOf('/');
            String methodName = url.substring(index+1,url.length());
            try {
                Method method = SQLiteJDBC.class.getMethod(methodName, DataRequest.class);
                return (DataResponse)method.invoke(SQLiteJDBC.getInstance(), request);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }else {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + url))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            request.add("username",AppStore.getJwt().getUsername());
            HttpClient client = HttpClient.newHttpClient();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("url=" + url +"    response.statusCode="+response.statusCode());
                if (response.statusCode() == 200) {
                                    System.out.println(response.body());
                    DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                    return dataResponse;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static DataResponse requestDataResponse(String url,DataRequest request){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + url))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                    .headers("Content-Type", "application/json")
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            request.add("username",AppStore.getJwt().getUsername());
            HttpClient client = HttpClient.newHttpClient();
            try {
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                System.out.println("url=" + url +"    response.statusCode="+response.statusCode());
                if (response.statusCode() == 200) {
                    return JsonConvertUtil.jsonToDataResponse(response.body());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        return null;
    }
    public static List requestDataList(String url,DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                .build();
        request.add("username",AppStore.getJwt().getUsername());
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("url=" + url +"    response.statusCode="+response.statusCode());
            if (response.statusCode() == 200) {
                System.out.println(response.body());
                return JsonConvertUtil.jsonToDataList(response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }
    public static Object requestDataObject(String url,DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                .build();
        request.add("username",AppStore.getJwt().getUsername());
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println("url=" + url +"    response.statusCode="+response.statusCode());
            if (response.statusCode() == 200) {
                return JsonConvertUtil.jsonToDataObject(response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     *  MyTreeNode requestTreeNode(String url, DataRequest request) 获取树节点对象
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return MyTreeNode 返回后台返回数据
     */
    public static MyTreeNode requestTreeNode(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                return gson.fromJson(response.body(), MyTreeNode.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<MyTreeNode> requestTreeNodeList(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                List list = gson.fromJson(response.body(),List.class);
                List<MyTreeNode> rList = new ArrayList<>();
                for(int i = 0; i < list.size();i++) {
                    rList.add(new MyTreeNode((Map)list.get(i)));
                }
                return rList;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *  List<OptionItem> requestOptionItemList(String url, DataRequest request) 获取OptionItemList对象
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return List<OptionItem> 返回后台返回数据
     */
    public static List<OptionItem> requestOptionItemList(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                OptionItemList o = gson.fromJson(response.body(), OptionItemList.class);
                if(o != null)
                return o.getItemList();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *   List<OptionItem> getDictionaryOptionItemList(String code) 获取数据字典OptionItemList对象
     * @param code  数据字典类型吗
     * @param
     * @return List<OptionItem> 返回后台返回数据
     */
    public static  List<OptionItem> getDictionaryOptionItemList(String code) {
        DataRequest req = new DataRequest();
        req.add("code", code);
        return requestOptionItemList("/api/base/getDictionaryOptionItemList",req);
    }

    /**
     *  byte[] requestByteData(String url, DataRequest request) 获取byte[] 对象 下载数据文件等
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param request 请求参数对象
     * @return List<OptionItem> 返回后台返回数据
     */
    public static byte[] requestByteData(String url, DataRequest request){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + url))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(request)))
                .headers("Content-Type", "application/json")
                .headers("Authorization", "Bearer "+AppStore.getJwt().getAccessToken())
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<byte[]>  response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
            if(response.statusCode() == 200) {
                return response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DataResponse uploadFile(String fileName,String remoteFile) 上传数据文件
     * @param fileName  本地文件名
     * @param remoteFile 远程文件路径
     * @return 上传操作信息
     */
    public static DataResponse uploadFile(String uri,String fileName,String remoteFile)  {
        try {
            Path file = Path.of(fileName);
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl+uri+"?remoteFile="+remoteFile))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .headers("Content-Type", "application/json")
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    //data为文件路径
    public static DataResponse uploadMultiFiles(String uri, List<String> paths, String remotePath) {
        try{
            Map<String , byte[]> data = new HashMap<>();
            paths.forEach(path -> {
                try{
                    File file = new File(path);
                    data.put(file.getName(), Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + uri + "?remotePath=" + remotePath))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(CommonMethod.mapToByteArray(data)))
                    .headers("Content-Type", "application/json")
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200){
                DataResponse res = gson.fromJson(response.body(), DataResponse.class);
                return res;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    //上传作业文件
    public static DataResponse uploadHomeworkFiles(List<String> paths, Integer homeworkId) {
        try{
            Map<String , byte[]> data = new HashMap<>();
            Map<String, String> nameMapper = new HashMap<>();
            paths.forEach(s -> {
                File f = new File(s);
                nameMapper.put(s, f.getName());
            });
            //重命名重名文件
            nameMapper.forEach((path, name) -> {
                final int[] k = {1};
                nameMapper.forEach((path2, name2) -> {
                    if(!path2.equals(path)){
                        if(name.equals(name2)){
                            nameMapper.put(path2, name2 + " (" + k[0] + ") ");
                            k[0]++;
                        }
                    }
                });
            });
            paths.forEach(path -> {
                try{
                    File file = new File(path);
                    data.put(nameMapper.get(path), Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(serverUrl + "/api/homework/uploadHomeworkFiles" + "?homeworkId=" + homeworkId))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(CommonMethod.mapToByteArray(data)))
                    .headers("Content-Type", "application/json")
                    .headers("Authorization", "Bearer " + AppStore.getJwt().getAccessToken())
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200){
                DataResponse res = gson.fromJson(response.body(), DataResponse.class);
                return res;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DataResponse importData(String url, String fileName, String paras) 导入数据文件
     * @param url  Web请求的Url 对用后的 RequestMapping
     * @param fileName 本地文件名
     * @param paras  上传参数
     * @return 导入结果信息
     */
    public static DataResponse importData(String url, String fileName, String paras)  {
        try {
            Path file = Path.of(fileName);
            String urlStr = serverUrl+url+"?uploader=HttpTestApp&fileName=" + file.getFileName() ;
            if(paras != null && paras.length() > 0)
                urlStr += "&"+paras;
            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .POST(HttpRequest.BodyPublishers.ofFile(file))
                    .build();
            HttpResponse<String>  response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                DataResponse dataResponse = gson.fromJson(response.body(), DataResponse.class);
                return dataResponse;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * DataResponse int uploadHtmlString(String html) 加密上传html模板字符串，用于生成htmp网页和PDF文件
     * @param html 上传的HTML字符串
     * @return html 序列号
     */

    public static int uploadHtmlString(String html)  {
            DataRequest req = new DataRequest();
            String str = new String(Base64.getEncoder().encode(html.getBytes(StandardCharsets.UTF_8)));
            req.add("html", str);
            DataResponse res =request("/api/base/uploadHtmlString",req);
            return CommonMethod.getIntegerFromObject(res.getData());
    }

}
