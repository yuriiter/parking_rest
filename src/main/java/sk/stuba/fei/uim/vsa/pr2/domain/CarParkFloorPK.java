package sk.stuba.fei.uim.vsa.pr2.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CarParkFloorPK implements Serializable {
    @Column(name = "CAR_PARK_FLOOR_IDENTIFIER")
    private String floorIdentifier;
    @Column(name = "CAR_PARK_ID")
    private Long carParkId;

    public CarParkFloorPK(){}
    public CarParkFloorPK(String floorIdentifier, Long carParkId) {
        this.floorIdentifier = floorIdentifier;
        this.carParkId = carParkId;
    }

    public String getFloorIdentifier() {
        return floorIdentifier;
    }

    public void setFloorIdentifier(String floorIdentifier) {
        this.floorIdentifier = floorIdentifier;
    }

    public Long getCarParkId() {
        return carParkId;
    }

    public void setCarParkId(Long carParkId) {
        this.carParkId = carParkId;
    }

    public void copyCarParkFloorPk(CarParkFloorPK carParkFloorPK) {
        this.floorIdentifier = carParkFloorPK.getFloorIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarParkFloorPK that = (CarParkFloorPK) o;
        return Objects.equals(floorIdentifier, that.floorIdentifier) && Objects.equals(carParkId, that.carParkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(floorIdentifier, carParkId);
    }

    @Override
    public String toString() {
        return "CarParkFloorPK{" +
                "floorIdentifier='" + floorIdentifier + '\'' +
                ", carParkId=" + carParkId +
                '}';
    }
}
