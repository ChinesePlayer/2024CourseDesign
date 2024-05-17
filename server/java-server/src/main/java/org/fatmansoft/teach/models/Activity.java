package org.fatmansoft.teach.models;
import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * Activity 校园活动实体类  保存校园活动的的基本信息
 * Integer ActivityId 活动参与人员表 主键 activityId
 * String name 活动名称
 * String address 活动地点
 * Integer qualityDevelopmentCredit 素拓分
 * Integer volunteerTime 志愿时长
 * String activityOrganizeUnit组织单位
 * Integer joinedPeople参与人数
 */
@Entity
@Table(name = "activity",
        uniqueConstraints = {
        })
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityId;
    @Size(max = 50)
    private String name;//活动名称
    private Integer qualityDevelopmentCredit;
    private Integer volunteerTime;
    @ManyToOne
    @JoinColumn(name="host_id")
    private Person person;
    private String address;
    private String activityOrganizeUnit;
    private Integer joinedPeople;
    private Integer leftPeopleRemaining;
    private String state;
    private String timeStart;
    private String timeEnd;
//    private Integer leftPeopleRemaining;
    public Integer getJoinedPeople() {
        return joinedPeople;
    }
    public void setJoinedPeople(Integer joinedPeople) {
        this.joinedPeople = joinedPeople;
    }
    public Integer getActivityId() {
        return activityId;
    }
    public void setActivityId(Integer ActivityId) {
        this.activityId = ActivityId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Integer getQualityDevelopmentCredit() {
        return qualityDevelopmentCredit;
    }
    public void setQualityDevelopmentCredit(Integer qualityDevelopmentCredit) {
        this.qualityDevelopmentCredit = qualityDevelopmentCredit;
    }
    public Integer getVolunteerTime() {
        return volunteerTime;
    }
    public void setVolunteerTime(Integer volunteerTime) {
        this.volunteerTime = volunteerTime;
    }
    public String getActivityOrganizeUnit() {
        return activityOrganizeUnit;
    }
    public void setActivityOrganizeUnit(String activityOrganizeUnit) {
        this.activityOrganizeUnit = activityOrganizeUnit;
    }

    public Integer getLeftPeopleRemaining() {
        return leftPeopleRemaining;
    }

    public void setLeftPeopleRemaining(Integer leftPeopleRemaining) {
        this.leftPeopleRemaining = leftPeopleRemaining;
    }

    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}