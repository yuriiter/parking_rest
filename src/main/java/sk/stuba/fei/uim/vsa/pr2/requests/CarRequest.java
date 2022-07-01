package sk.stuba.fei.uim.vsa.pr2.requests;


import java.util.List;

public class CarRequest {
    private Long id;
    private String brand;
    private String model;
    private String colour;
    private String vrp;
    private UserRequest owner;
    private List<ReservationRequest> reservations;

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

    public UserRequest getOwner() {
        return owner;
    }

    public void setOwner(UserRequest owner) {
        this.owner = owner;
    }

    public List<ReservationRequest> getReservations() {
        return reservations;
    }

    public void setReservations(List<ReservationRequest> reservations) {
        this.reservations = reservations;
    }
}
