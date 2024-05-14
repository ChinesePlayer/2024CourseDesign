package org.fatmansoft.teach.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.fatmansoft.teach.models.Honor;
import org.fatmansoft.teach.models.Person;
import org.fatmansoft.teach.models.Score;
import org.fatmansoft.teach.models.Student;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.ScoreRepository;
import org.fatmansoft.teach.repository.StudentRepository;
import org.fatmansoft.teach.util.ComDataUtil;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Value("${attach.folder}")
    private String attachFolder;

    public Map getMapFromStudent(Student s) {
        Map m = new HashMap();
        Person p;
        if(s == null)
            return m;
        m.put("major",s.getMajor());
        m.put("className",s.getClassName());
        p = s.getPerson();
        if(p == null)
            return m;
        m.put("studentId", s.getStudentId());
        m.put("personId", p.getPersonId());
        m.put("num",p.getNum());
        m.put("name",p.getName());
        m.put("dept",p.getDept());
        m.put("card",p.getCard());
        String gender = p.getGender();
        m.put("gender",gender);
        m.put("genderName", ComDataUtil.getInstance().getDictionaryLabelByValue("XBM", gender)); //性别类型的值转换成数据类型名
        m.put("birthday", p.getBirthday());  //时间格式转换字符串
        m.put("email",p.getEmail());
        m.put("phone",p.getPhone());
        m.put("address",p.getAddress());
        m.put("introduce",p.getIntroduce());
        return m;
    }

    public DataResponse getStudentIntroHTML(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Optional<Student> sOp = studentRepository.findById(studentId);
        if(sOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无法获取学生信息");
        }
        Student student = sOp.get();
        Person p = student.getPerson();
        String html = p.getIntroduce();
        return CommonMethod.getReturnData(html);
    }

    public DataResponse getStudentDashboardInfo(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        if(studentId == null){
            return CommonMethod.getReturnMessageError("无法获取学生ID");
        }
        Optional<Student> stuOp = studentRepository.findById(studentId);
        if(stuOp.isEmpty()){
            return CommonMethod.getReturnMessageError("无效的学生ID");
        }
        Student student = stuOp.get();
        Map data = new HashMap<>();
        data.put("studentId", studentId);
        data.put("studentNum", student.getPerson().getNum());
        data.put("studentName", student.getPerson().getName());
        data.put("studentDept", student.getPerson().getDept());
        //TODO: 暂时先返回这些数据，后续会逐渐增加
        return CommonMethod.getReturnData(data);
    }

    public ResponseEntity<StreamingResponseBody> getIntroducePdf(DataRequest req){
        Integer studentId = req.getInteger("studentId");
        if (studentId == null){
            return ResponseEntity.internalServerError().build();
        }
        Optional<Student> studentOp = studentRepository.findById(studentId);
        if(studentOp.isEmpty()){
            return ResponseEntity.internalServerError().build();
        }
        Student student = studentOp.get();
        List<Score> scoreList = scoreRepository.findByStudentId(studentId);
        //已通过的课程
        List<Score> passedScore = new ArrayList<>();
        List<Score> nonReadingScore = new ArrayList<>();
        final double[] gpas = {0};
        scoreList.forEach(score -> {
            if(score.getStatus() == 1){
                //记录已通过的课程
                passedScore.add(score);
                //统计绩点
                gpas[0] += score.calcGpa();
            }
            if(score.getStatus() != 0){
                //统计非正在修读中的课程
                nonReadingScore.add(score);
            }
        });
        //计算绩点
        double gpa = gpas[0]/nonReadingScore.size();
        //搜集学生信息，构建PDF
        String name = student.getPerson().getName();
        //学号
        String num = student.getPerson().getNum();
        String dept = student.getPerson().getDept();
        String major = student.getMajor();
        String className = student.getClassName();
        String loc = student.getPerson().getAddress();
        //证件号码
        String idNum = student.getPerson().getCard();
        String gender = student.getPerson().getGender();
        String birth = student.getPerson().getBirthday();
        String email = student.getPerson().getEmail();
        String phone = student.getPerson().getPhone();
        String address = student.getPerson().getAddress();

        List<Honor> honorList = student.getHonors();

        Document doc = new Document(PageSize.A4, 30,30,40,40);
        try{
            ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, byteOutStream);
            doc.open();

            //学生基本信息表格
            PdfPTable basicInfoTable = new PdfPTable(4);
            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.BOLD);
            Font contentFont = new Font(chineseFont, 12);


            //添加标题
            Paragraph title = new Paragraph(name + " 个人简介", titleFont);
            //标题居中显示
            title.setAlignment(Element.ALIGN_CENTER);
            //与下一段的距离
            title.setSpacingAfter(20);
            doc.add(title);


//            //添加头像
//            //读取图片
//            Image image = Image.getInstance(CommonMethod.getAvatar(student.getPerson().getPersonId(), attachFolder));
//            //头像未获取成功, 使用默认头像
//            if(image == null){
//                image = Image.getInstance(CommonMethod.getDefaultAvatar());
//            }
//            PdfPCell imageCell = new PdfPCell();
//            imageCell.setRowspan(2);
//            imageCell.setColspan(2);
//            imageCell.addElement(image);


            String introduction = "    " + name + "，学号为" + num + "，就读于山东大学" + dept + "学院的" + major + "专业" +
                    "，所在班级为" + className + "。" + "联系方式: 邮箱 " + email + "\n电话 " + phone + "。" ;
            doc.add(new Paragraph(introduction, contentFont));
//            basicInfoTable.addCell(new Paragraph("姓名: "+name, contentFont));
//            basicInfoTable.addCell(new Paragraph("学号: "+num, contentFont));
//            basicInfoTable.addCell(new Paragraph("专业: "+major, contentFont));
//            basicInfoTable.addCell(new Paragraph("学院: "+dept, contentFont));
//            basicInfoTable.addCell(new Paragraph("班级: "+className, contentFont));
//            basicInfoTable.addCell(new Paragraph("证件号码: "+idNum, contentFont));
//            basicInfoTable.addCell(new Paragraph("性别: "+gender, contentFont));
//            basicInfoTable.addCell(new Paragraph("生日: "+birth, contentFont));
//            basicInfoTable.addCell(new Paragraph("邮箱: "+email, contentFont));
//            basicInfoTable.addCell(new Paragraph("电话: "+phone, contentFont));
//            basicInfoTable.addCell(new Paragraph("地址: "+address, contentFont));
            doc.add(basicInfoTable);

            doc.close();

            byte[] pdfBytes = byteOutStream.toByteArray();
            StreamingResponseBody streamBody = outputStream -> outputStream.write(pdfBytes);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(streamBody);
        } catch (DocumentException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
