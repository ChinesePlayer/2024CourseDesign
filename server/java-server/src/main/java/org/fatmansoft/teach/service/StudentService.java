package org.fatmansoft.teach.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.fatmansoft.teach.models.*;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.CourseRepository;
import org.fatmansoft.teach.repository.PersonRepository;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private PersonRepository personRepository;
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
        m.put("studentNum",p.getPersonNum());
        m.put("studentName",p.getPersonName());
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
        data.put("studentNum", student.getPerson().getPersonNum());
        data.put("studentName", student.getPerson().getPersonName());
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
        String name = student.getPerson().getPersonName();
        //学号
        String num = student.getPerson().getPersonNum();
        String dept = student.getPerson().getDept();
        String major = student.getMajor();
        String className = student.getClassName();
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
            BaseFont chineseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            Font titleFont = new Font(chineseFont, 24, Font.BOLD);
            Font subtitleFont = new Font(chineseFont, 18, Font.BOLD);
            Font contentFont = new Font(chineseFont, 12);


            //添加标题
            Paragraph title = new Paragraph("个人画像", titleFont);
            //标题居中显示
            title.setAlignment(Element.ALIGN_CENTER);
            //与下一段的距离
            title.setSpacingAfter(20);
            doc.add(title);


            //添加头像
            //读取图片
            Image image = Image.getInstance(CommonMethod.getAvatar(student.getPerson().getPersonId(), attachFolder));
            //头像未获取成功, 使用默认头像
            if(image == null){
                image = Image.getInstance(CommonMethod.getDefaultAvatar());
            }
            PdfPCell imageCell = new PdfPCell();
            image.scaleToFit(200,200);
            //图片横跨表格两行四列
            imageCell.setRowspan(2);
            imageCell.setColspan(4);
            //设置图片未居中对齐
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imageCell.setVerticalAlignment(Element.ALIGN_CENTER);
            image.setAlignment(Image.ALIGN_CENTER);
            imageCell.addElement(image);

            PdfPTable basicInfoTable = new PdfPTable(4);
            basicInfoTable.setSpacingAfter(20);
            //注意添加单元格时单元格数量一定要是列数的倍数，否则会有部分单元格无法被添加
            basicInfoTable.addCell(new PdfPCell(new Paragraph("姓名: "+name, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("学号: "+num, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("专业: "+major, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("学院: "+dept, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("班级: "+className, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("证件号码: "+idNum, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("性别: "+gender, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("生日: "+birth, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("邮箱: "+email, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("电话: "+phone, contentFont)));
            basicInfoTable.addCell(new PdfPCell(new Paragraph("地址: "+address, contentFont)));
            //新增一个空白单元格，凑成列数的倍数
            basicInfoTable.addCell(new PdfPCell(new Paragraph(" ", contentFont)));
            basicInfoTable.addCell(imageCell);
            doc.add(basicInfoTable);

            //添加荣誉信息
            Paragraph honorTitle = new Paragraph("荣誉墙", subtitleFont);
            honorTitle.setAlignment(Element.ALIGN_CENTER);
            honorTitle.setSpacingAfter(10);
            doc.add(honorTitle);

            doc.add(new Paragraph("荣誉称号: ", contentFont));
            for(Honor h : getHonorByType(honorList, EHonorType.HONOR_TITLE)){
                Paragraph unit = new Paragraph("    " + h.getHonorContent(), contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }
            if(getHonorByType(honorList, EHonorType.HONOR_TITLE).isEmpty()){
                Paragraph unit = new Paragraph("    暂无", contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }

            doc.add(new Paragraph("学科竞赛: ", contentFont));
            for(Honor h : getHonorByType(honorList, EHonorType.HONOR_CONTEST)){
                Paragraph unit = new Paragraph("    " + h.getHonorContent(), contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }
            if(getHonorByType(honorList, EHonorType.HONOR_CONTEST).isEmpty()){
                Paragraph unit = new Paragraph("    暂无", contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }

            doc.add(new Paragraph("社会实践: ", contentFont));
            for(Honor h : getHonorByType(honorList, EHonorType.HONOR_PRACTICE)){
                Paragraph unit = new Paragraph("    " + h.getHonorContent(), contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }
            if(getHonorByType(honorList, EHonorType.HONOR_PRACTICE).isEmpty()){
                Paragraph unit = new Paragraph("    暂无", contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }

            doc.add(new Paragraph("科技成果: ", contentFont));
            for(Honor h : getHonorByType(honorList, EHonorType.HONOR_TECH)){
                Paragraph unit = new Paragraph("    " + h.getHonorContent(), contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }
            if(getHonorByType(honorList, EHonorType.HONOR_TECH).isEmpty()){
                Paragraph unit = new Paragraph("    暂无", contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }

            doc.add(new Paragraph("培训讲座: ", contentFont));
            for(Honor h : getHonorByType(honorList, EHonorType.HONOR_LECTURE)){
                Paragraph unit = new Paragraph("    " + h.getHonorContent(), contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }
            if(getHonorByType(honorList, EHonorType.HONOR_LECTURE).isEmpty()){
                Paragraph unit = new Paragraph("    暂无", contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }

            doc.add(new Paragraph("校外实习: ", contentFont));
            for(Honor h : getHonorByType(honorList, EHonorType.HONOR_INTERNSHIP)){
                Paragraph unit = new Paragraph("    " + h.getHonorContent(), contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }
            if(getHonorByType(honorList, EHonorType.HONOR_INTERNSHIP).isEmpty()){
                Paragraph unit = new Paragraph("    暂无", contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }

            doc.add(new Paragraph("创新项目: ", contentFont));
            for(Honor h : getHonorByType(honorList, EHonorType.HONOR_PROJ)){
                Paragraph unit = new Paragraph("    " + h.getHonorContent(), contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }
            if(getHonorByType(honorList, EHonorType.HONOR_PROJ).isEmpty()){
                Paragraph unit = new Paragraph("    暂无", contentFont);
                unit.setSpacingAfter(5);
                doc.add(unit);
            }

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

    public DataResponse importStudentExcel(MultipartFile[] files, DataRequest req){
        int total;
        int count = 0;
        if(files == null){
            return CommonMethod.getReturnMessageError("请选择学生文件! ");
        }
        if(files.length != 1){
            return CommonMethod.getReturnMessageError("一次只能上传一个一个文件! ");
        }
        try{
            byte[] bytesData = files[0].getBytes();
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytesData);
            XSSFWorkbook workbook = new XSSFWorkbook(byteInputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            if(sheet.getLastRowNum() + 1 <= 1){
                return CommonMethod.getReturnMessageOK();
            }
            total = sheet.getLastRowNum() + 1;
            List<Student> toBeSaved = new ArrayList<>();
            List<Person> toBeSavedPerson = new ArrayList<>();
            for(int i = 1; i < total; i++){
                XSSFRow row = sheet.getRow(i);
                XSSFCell cell = row.getCell(1);
                String studentNum = cell.getStringCellValue();

                Student student = null;
                Person person = null;
                Optional<Student> sOp = studentRepository.findByPersonPersonNum(studentNum);
                Optional<Person> pOp = personRepository.findByPersonNum(studentNum);
                if(sOp.isPresent()){
                    student = sOp.get();
                }
                if(student == null){
                    student = new Student();
                }
                if(pOp.isPresent()){
                    person = pOp.get();
                }
                if(person == null){
                    person = new Person();
                }

                person.setPersonNum(studentNum);
                person.setType("1");

                cell = row.getCell(2);
                String studentName = cell.getStringCellValue();
                person.setPersonName(studentName);

                cell = row.getCell(3);
                String dept = cell.getStringCellValue();
                person.setDept(dept);

                cell = row.getCell(4);
                String major = cell.getStringCellValue();
                student.setMajor(major);

                cell = row.getCell(5);
                String className = cell.getStringCellValue();
                student.setClassName(className);

                cell = row.getCell(6);
                String card = cell.getStringCellValue();
                person.setCard(card);

                cell = row.getCell(7);
                String gender = cell.getStringCellValue();
                person.setGender(CommonMethod.getGenderCode(gender));

                cell = row.getCell(8);
                String birth = cell.getStringCellValue();
                person.setBirthday(birth);

                cell = row.getCell(9);
                String email = cell.getStringCellValue();
                //检查邮箱是否合法
                if(!CommonMethod.isValidEmail(email)){
                    continue;
                }
                person.setEmail(email);

                cell = row.getCell(10);
                String phone = cell.getStringCellValue();
                person.setPhone(phone);

                cell = row.getCell(11);
                String address = cell.getStringCellValue();
                person.setPhone(address);

                student.setPerson(person);
                toBeSaved.add(student);
                toBeSavedPerson.add(person);
                System.out.println(studentName);
                count++;
            }
            personRepository.saveAllAndFlush(toBeSavedPerson);
            studentRepository.saveAll(toBeSaved);
        }
        catch (IOException e){
            e.printStackTrace();
            return CommonMethod.getReturnMessageError("无法读取表格文件! ");
        }
        Map dataMap = new HashMap<>();
        dataMap.put("success",count);
        dataMap.put("total",total);
        return CommonMethod.getReturnData(dataMap);

    }

    public List<Honor> getHonorByType(List<Honor> list, EHonorType type){
        List<Honor> res = new ArrayList<>();
        for(Honor h : list){
            if(h.getHonorType().getType() == type){
                res.add(h);
            }
        }
        return res;
    }

    public DataResponse getStudentsListByCourseId(DataRequest req) {
        Integer courseId = req.getInteger("courseId");
        if(courseId == null){
            return CommonMethod.getReturnMessageError("无法获取课程信息");
        }
        List<Student> studentList = courseRepository.findStudentsByCourseId(courseId);
        List<Map> dataList = new ArrayList<>();
        for(Student s : studentList){
            dataList.add(getMapFromStudent(s));
        }
        return CommonMethod.getReturnData(dataList);
    }

}
