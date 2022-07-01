package sk.stuba.fei.uim.vsa.pr2.requests;

public class ReservationRequest {
    private Long id;
    private String start;
    private String end;
    private CarRequest car;
    private ParkingSpotRequest spot;
    private Double prices;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public CarRequest getCar() {
        return car;
    }

    public void setCar(CarRequest car) {
        this.car = car;
    }

    public ParkingSpotRequest getSpot() {
        return spot;
    }

    public void setSpot(ParkingSpotRequest spot) {
        this.spot = spot;
    }

    public Double getPrices() {
        return prices;
    }

    public void setPrices(Double prices) {
        this.prices = prices;
    }
}
