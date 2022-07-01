package sk.stuba.fei.uim.vsa.pr2.requests;

import java.util.List;

public class ParkingSpotRequest {
    private Long id;
    private String identifier;
    private String carParkFloor;
    private Long carPark;
    private Boolean free;
    private List<ReservationRequest> reservations;


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

    public List<ReservationRequest> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationRequest> reservations) {
        this.reservations = reservations;
    }
}
