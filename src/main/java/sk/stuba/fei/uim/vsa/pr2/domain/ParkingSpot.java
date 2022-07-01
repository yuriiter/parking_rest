package sk.stuba.fei.uim.vsa.pr2.domain;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="PARKING_SPOT")
public class ParkingSpot implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARKING_SPOT_ID")
    private Long parkingSpotId;

    @Column(name = "SPOT_IDENTIFIER")
    private String spotIdentifier;

    @OneToOne
    private Car car;

    public ParkingSpot(Long parkingSpotId, String spotIdentifier) {
        this.parkingSpotId = parkingSpotId;
        this.spotIdentifier = spotIdentifier;
    }

    public void setCar(Car car) {
        this.car = car;
    }
    public Car getCar() {
        return car;
    }

    public ParkingSpot() {}
    public ParkingSpot(String spotIdentifier) {
        this.spotIdentifier = spotIdentifier;
    }

    public String getSpotIdentifier() {
        return spotIdentifier;
    }

    public Long getParkingSpotId() {
        return parkingSpotId;
    }


    @Override
    public String toString() {
        return "ParkingSpot{" +
                "parkingSpotId=" + parkingSpotId +
                ", spotIdentifier='" + spotIdentifier + '\'' +
                ", car=" + car +
                '}';
    }
}