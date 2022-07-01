package sk.stuba.fei.uim.vsa.pr2.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name="CAR_PARK")
public class CarPark implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CAR_PARK_ID")
    private Long carParkId;

    @Column(name = "CAR_PARK_NAME")
    private String name;

    @Column(name = "CAR_PARK_ADDRESS")
    private String address;

    @Column(name = "CAR_PARK_PRICE_PER_HOUR")
    private Integer pricePerHour;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
    private List<CarParkFloor> carParkFloors;

    public CarPark() {}
    public CarPark(String name, String address, Integer pricePerHour) {
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
    }

    public void addCarParkFloor(CarParkFloor carParkFloor) {
        if(carParkFloors == null) {
            carParkFloors = new ArrayList<>();
        }
        carParkFloors.add(carParkFloor);
    }

    public void copyCarParkWithoutFloors(CarPark carPark) {
        this.name = carPark.getName();
        this.address = carPark.getAddress();
        this.pricePerHour = carPark.getPricePerHour();
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

    public Integer getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(Integer pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public List<CarParkFloor> getCarParkFloors() {
        return carParkFloors;
    }

    public void removeParkFloor(CarParkFloor carParkFloor) {
        if(carParkFloors == null) {
            return;
        }
        Optional<CarParkFloor> carParkFloorOptional =
                carParkFloors.stream().filter(val -> val.getFloorIdentifier().equals(carParkFloor.getFloorIdentifier()))
                                .findAny();
        if(carParkFloorOptional.isPresent()) {
            CarParkFloor result = carParkFloorOptional.get();
            carParkFloors.remove(result);
        }
    }

    public Long getCarParkId() {
        return carParkId;
    }
    public void setCarParkId(Long carParkId) {
        this.carParkId = carParkId;
    }

    @Override
    public String toString() {
        return "CarPark{" +
                "carParkId=" + carParkId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", pricePerHour=" + pricePerHour +
                ", carParkFloors=" + carParkFloors +
                '}';
    }
}
