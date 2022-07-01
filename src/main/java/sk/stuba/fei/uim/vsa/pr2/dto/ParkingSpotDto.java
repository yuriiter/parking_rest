package sk.stuba.fei.uim.vsa.pr2.dto;

import sk.stuba.fei.uim.vsa.pr2.domain.Car;

import java.util.List;


public class ParkingSpotDto {
    private Long id;
    private String identifier;
    private String carParkFloor;
    private Long carPark;
    private Boolean free;
    private List<Long> reservations;


    public ParkingSpotDto() {}

    public ParkingSpotDto(Long id, String identifier,
                          String carParkFloor, Long carPark,
                          Boolean free, List<Long> reservations) {
        this.id = id;
        this.identifier = identifier;
        this.carParkFloor = carParkFloor;
        this.carPark = carPark;
        this.free = free;
        this.reservations = reservations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCarParkFloor() {
        return carParkFloor;
    }

    public void setCarParkFloor(String carParkFloor) {
        this.carParkFloor = carParkFloor;
    }

    public Long getCarPark() {
        return carPark;
    }

    public void setCarPark(Long carPark) {
        this.carPark = carPark;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public List<Long> getReservations() {
        return reservations;
    }

    public void setReservations(List<Long> reservations) {
        this.reservations = reservations;
    }
}
