package org.fatmansoft.teach.controllers;

import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.AttendanceRepository;
import org.fatmansoft.teach.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/getAttendanceList")
    public DataResponse getAttendanceList(@Valid @RequestBody DataRequest req){
        return attendanceService.getAttendanceList(req);
    }

    @PostMapping("/saveAttendance")
    public DataResponse saveAttendance(@Valid @RequestBody DataRequest req){
        return attendanceService.attendanceEditSave(req);
    }

    @PostMapping("/deleteAttendance")
    public DataResponse deleteAttendance(@Valid @RequestBody DataRequest req){
        return attendanceService.attendanceDelete(req);
    }

    //学生端获取该学生的自己的考勤信息
    @PostMapping("/getStudentAttendanceList")
    public DataResponse getStudentAttendanceList(@Valid @RequestBody DataRequest req){
        return attendanceService.getStudentAttendanceList(req);
    }

    //教师端获取该教师的课程的考勤信息
    @PostMapping("/getTeacherAttendanceList")
    public DataResponse getTeacherAttendanceList(@Valid @RequestBody DataRequest req){
        return attendanceService.getTeacherAttendanceList(req);
    }
}
