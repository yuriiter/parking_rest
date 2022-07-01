package sk.stuba.fei.uim.vsa.pr2.dto;

import sk.stuba.fei.uim.vsa.pr2.domain.CarPark;

import java.util.List;
import java.util.stream.Collectors;

public class CarParkDto {
    private Long id;
    private String name;
    private String address;
    private Integer pricePerHour;
    private List<String> floors;

    public CarParkDto() {}
    public CarParkDto(String name, String address, Integer pricePerHour) {
        this.name = name;
        this.address = address;
        this.pricePerHour = pricePerHour;
    }

    public CarParkDto(CarPark carPark) {
        this.id = carPark.getCarParkId();
        this.name = carPark.getName();
        this.address = carPark.getAddress();
        this.pricePerHour = carPark.getPricePerHour();
        this.floors = carPark.getCarParkFloors()
                .stream()
                .map(floor -> floor.getCarParkFloorPK().getFloorIdentifier())
                .collect(Collectors.toList());
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

    public List<String> getFloors() {
        return floors;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
