package sk.stuba.fei.uim.vsa.pr2.requests;

import java.util.List;

public class CarParkFloorRequest {
    private String identifier;
    private Long carPark;
    private List<ParkingSpotRequest> spots;

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

    public List<ParkingSpotRequest> getSpots() {
        return spots;
    }

    public void setSpots(List<ParkingSpotRequest> spots) {
        this.spots = spots;
    }
}
