package sk.stuba.fei.uim.vsa.pr2.dto;

import sk.stuba.fei.uim.vsa.pr2.domain.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReservationDto {
    private Long id;
    private String start;
    private String end;
    private Long car;
    private Long spot;
    private Double prices;

    public ReservationDto() {}

    public ReservationDto(Reservation reservation) {
        this.id = reservation.getReservationId();
        this.prices = reservation.getTotalPrice();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        this.start = dateFormat.format(reservation.getStartDate());
        if(reservation.getEndDate() != null) {
            this.end = dateFormat.format(reservation.getEndDate());
        }
        this.car = reservation.getCar().getCarId();
        this.spot = reservation.getParkingSpot().getParkingSpotId();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public boolean setStart(Date start) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            this.start = dateFormat.format(start);
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }


    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean setEnd(Date end) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            this.end = dateFormat.format(end);
        }
        catch(Exception e) {
            return false;
        }
        return true;
    }

    public Long getCar() {
        return car;
    }

    public void setCar(Long car) {
        this.car = car;
    }

    public Long getSpot() {
        return spot;
    }

    public void setSpot(Long spot) {
        this.spot = spot;
    }

    public Double getPrices() {
        return prices;
    }

    public void setPrices(Double prices) {
        this.prices = prices;
    }
}
