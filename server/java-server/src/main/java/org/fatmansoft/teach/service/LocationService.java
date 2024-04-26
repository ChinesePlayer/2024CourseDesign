package org.fatmansoft.teach.service;

import org.fatmansoft.teach.models.CourseLocation;
import org.fatmansoft.teach.payload.request.DataRequest;
import org.fatmansoft.teach.payload.response.DataResponse;
import org.fatmansoft.teach.repository.LocationRepository;
import org.fatmansoft.teach.util.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;
    public DataResponse getCourseLocationList(@Valid @RequestBody DataRequest req){
        List<CourseLocation> locations = locationRepository.findAll();
        List<Map> dataList = new ArrayList<>();
        for(CourseLocation cl : locations){
            Map m = new HashMap();
            m.put("locationId", cl.getLocationId()+"");
            m.put("value", cl.getValue());
            dataList.add(m);
        }
        return CommonMethod.getReturnData(dataList);
    }
}
