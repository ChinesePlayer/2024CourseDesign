package org.fatmansoft.teach.models;

import javax.persistence.*;

@Entity
@Table(name = "person_activity", uniqueConstraints = {

})
public class PersonActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personActivityId;
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    private boolean attendance;//是否参与
    private String state;

    public Integer getPersonActivityId() {
        return personActivityId;
    }

    public void setPersonActivityId(Integer personActivityId) {

        this.personActivityId = personActivityId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public boolean isAttendance() {

        return attendance;
    }

    public void setAttendance(boolean attendance) {

        this.attendance = attendance;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
