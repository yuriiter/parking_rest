package sk.stuba.fei.uim.vsa.pr2.domain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="CAR")
public class Car implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAR_ID")
    private Long carId;

    @Column(name = "BRAND")
    private String brand;

    @Column(name = "MODEL")
    private String model;

    @Column(name = "COLOUR")
    private String colour;

    @Column(name = "VEHICLE_REGISTRATION_PLATE")
    private String vehicleRegistrationPlate;

    @ManyToOne
    private User user;

    public Car(Long carId, String brand, String model, String colour, String vehicleRegistrationPlate) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.colour = colour;
        this.vehicleRegistrationPlate = vehicleRegistrationPlate;
    }

    public Car() {}
    public Car(String brand, String model, String colour, String vehicleRegistrationPlate) {
        this.brand = brand;
        this.model = model;
        this.colour = colour;
        this.vehicleRegistrationPlate = vehicleRegistrationPlate;
    }

    public void copyCar(Car car) {
        this.brand = car.getBrand();
        this.colour = car.getColour();
        this.vehicleRegistrationPlate = car.getVehicleRegistrationPlate();
        this.model = car.getModel();
    }

    public User getUser() {
        return user;
    }

    public Long getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getColour() {
        return colour;
    }

    public String getVehicleRegistrationPlate() {
        return vehicleRegistrationPlate;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", colour='" + colour + '\'' +
                ", vehicleRegistrationPlate='" + vehicleRegistrationPlate + '\'' +
                ", user=" + user +
                '}';
    }
}