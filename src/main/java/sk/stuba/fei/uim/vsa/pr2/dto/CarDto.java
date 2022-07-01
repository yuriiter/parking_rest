package sk.stuba.fei.uim.vsa.pr2.dto;

import sk.stuba.fei.uim.vsa.pr2.domain.Car;

import java.util.List;

public class CarDto {
    private Long id;
    private String brand;
    private String model;
    private String colour;
    private String vrp;
    private Long owner;
    private List<Long> reservations;

    public CarDto() {}

    public CarDto(Long id, String brand, String model, String colour, String vrp, Long owner, List<Long> reservations) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.colour = colour;
        this.vrp = vrp;
        this.owner = owner;
        this.reservations = reservations;
    }

    public CarDto(Car car) {
        this.id = car.getCarId();
        this.brand = car.getBrand();
        this.model = car.getModel();
        this.colour = car.getColour();
        this.vrp = car.getVehicleRegistrationPlate();
        this.owner = car.getUser().getUserId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getVrp() {
        return vrp;
    }

    public void setVrp(String vrp) {
        this.vrp = vrp;
    }

    public Long getOwner() {
        return owner;
    }

    public void setOwner(Long owner) {
        this.owner = owner;
    }

    public List<Long> getReservations() {
        return reservations;
    }

    public void setReservations(List<Long> reservations) {
        this.reservations = reservations;
    }
}
