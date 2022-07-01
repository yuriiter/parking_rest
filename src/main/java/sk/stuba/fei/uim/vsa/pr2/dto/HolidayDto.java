package sk.stuba.fei.uim.vsa.pr2.dto;

import sk.stuba.fei.uim.vsa.pr2.domain.Holiday;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HolidayDto {
    private Long id;
    private String name;
    private String date;

    public HolidayDto() {}

    public HolidayDto(Holiday holiday) {
        this.id = holiday.getHolidayId();
        this.name = holiday.getName();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.date = dateFormat.format(holiday.getDate());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public boolean setDate(Date date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            this.date = dateFormat.format(date);
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
