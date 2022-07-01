package sk.stuba.fei.uim.vsa.pr2.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="HOLIDAY")
public class Holiday implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOLIDAY_ID")
    private Long holidayId;

    @Column(name = "NAME")
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE")
    private Date date;

    public Long getHolidayId() {
        return holidayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setHolidayId(Long holidayId) {
        this.holidayId = holidayId;
    }

    public Holiday() {}
    public Holiday(String name, Date date) {
        this.date = date;
        this.name = name;
    }
    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "holidayId=" + holidayId +
                ", name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}
