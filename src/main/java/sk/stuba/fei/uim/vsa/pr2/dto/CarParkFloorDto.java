package sk.stuba.fei.uim.vsa.pr2.dto;

import sk.stuba.fei.uim.vsa.pr2.domain.CarParkFloor;

import java.util.List;
import java.util.stream.Collectors;

public class CarParkFloorDto {
    private String identifier;
    private Long carPark;
    private List<Long> spots;


    public CarParkFloorDto() {}
    public CarParkFloorDto(CarParkFloor carParkFloor) {
        this.carPark = carParkFloor.getCarParkFloorPK().getCarParkId();
        this.identifier = carParkFloor.getFloorIdentifier();
        this.spots = carParkFloor.getParkingSpots().stream()
                .map(spot -> spot.getParkingSpotId())
                .collect(Collectors.toList());
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Long getCarPark() {
        return carPark;
    }

    public void setCarPark(Long carPark) {
        this.carPark = carPark;
    }

    public List<Long> getSpots() {
        return spots;
    }

    public void setSpots(List<Long> spots) {
        this.spots = spots;
    }
}
