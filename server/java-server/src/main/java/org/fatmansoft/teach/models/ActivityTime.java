package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(name = "activity_time",
        uniqueConstraints = {
        })
public class ActivityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer activityTimeId;
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
    private String activityStartTime;
    private String activityEndTime;

    public Integer getActivityTimeId() {
        return activityTimeId;
    }

    public void setActivityTimeId(Integer activityTimeId) {
        this.activityTimeId = activityTimeId;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getActivityStartTime() {
        return activityStartTime;
    }

    public void setActivityStartTime(String activityStartTime) {
        this.activityStartTime = activityStartTime;
    }

    public String getActivityEndTime() {
        return activityEndTime;
    }

    public void setActivityEndTime(String activityEndTime) {
        this.activityEndTime = activityEndTime;
    }
}
