package sk.stuba.fei.uim.vsa.pr2.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Entity
@Table(name="CAR_PARK_FLOOR")
public class CarParkFloor implements Serializable {
    @EmbeddedId
    private CarParkFloorPK carParkFloorPK;

    @ManyToOne
    @MapsId("carParkId")
    private CarPark carPark;

    @OneToMany(cascade=CascadeType.REMOVE, orphanRemoval=true, fetch=FetchType.EAGER)
    private List<ParkingSpot> parkingSpots;


    public CarParkFloor() {}
    public CarParkFloor(String floorIdentifier, CarPark carPark) {
        this.carPark = carPark;
        this.carParkFloorPK = new CarParkFloorPK(floorIdentifier, carPark.getCarParkId());
    }

    public void copyCarParkFloorWithoutLinks(CarParkFloor carParkFloor) {
        this.carParkFloorPK.copyCarParkFloorPk(carParkFloor.getCarParkFloorPK());
    }

    public CarParkFloorPK getCarParkFloorPK() {
        return carParkFloorPK;
    }

    public void setCarParkFloorPK(CarParkFloorPK carParkFloorPK) {
        this.carParkFloorPK = carParkFloorPK;
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        if(parkingSpots == null) {
            parkingSpots = new ArrayList<>();
        }
        parkingSpots.add(parkingSpot);
    }

    public String getFloorIdentifier() {
        if(carParkFloorPK == null) {
            return null;
        }
        return carParkFloorPK.getFloorIdentifier();
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }
    public void removeParkingSpot(ParkingSpot parkingSpot) {
        if(parkingSpots == null) {
            return;
        }
        Optional<ParkingSpot> parkingSpotOptional = parkingSpots.stream()
                        .filter(parkingSpotFiltered -> parkingSpotFiltered.getParkingSpotId().equals(parkingSpot.getParkingSpotId()))
                        .findAny();
        if(!parkingSpotOptional.isPresent()) {
            return;
        }
        parkingSpots.remove(parkingSpotOptional.get());
    }

    @Override
    public String toString() {
        return "CarParkFloor{" +
                "carParkFloorPK=" + carParkFloorPK +
                ", parkingSpots=" + parkingSpots +
                '}';
    }
}

