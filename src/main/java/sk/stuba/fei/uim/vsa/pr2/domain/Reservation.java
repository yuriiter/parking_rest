package sk.stuba.fei.uim.vsa.pr2.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name="RESERVATION")
public class Reservation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESERVATION_ID")
    private Long reservationId;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    private Date endDate;

    @ManyToOne
    private Car car;
    @ManyToOne
    private ParkingSpot parkingSpot;
    private Double totalPrice;

    public Reservation() {}

    public Reservation(Car car, ParkingSpot parkingSpot) {
        this.startDate = new Date();
        this.car = car;
        this.parkingSpot = parkingSpot;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    private static boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    public Long getReservationId() {
        return reservationId;
    }

    private void countTotalPrice(Integer pricePerHour, List<Object> holidaysObjects) {
        Double totalPrice = 0d;
        long currentHourStart = startDate.getTime();
        long reservationEndTimestamp = endDate.getTime();

        List<Holiday> holidays = holidaysObjects
                .stream().map(holidaysObject -> (Holiday) holidaysObject)
                .collect(Collectors.toList());
        List<LocalDate> holidaysStartDays = holidays.stream()
                .map(holiday -> new java.sql.Date(holiday.getDate().getTime()).toLocalDate())
                .collect(Collectors.toList());

        while(currentHourStart < reservationEndTimestamp) {
            double coeff = 1;
            for(LocalDate date : holidaysStartDays) {
                int holidayDayOfMonth = date.getDayOfMonth();
                int holidayMonth = date.getMonthValue();
                int holidayYear = date.getYear();

                LocalDate currentHourStartLD = new java.sql.Date(currentHourStart).toLocalDate();
                int currentHourStartDayOfMonth = currentHourStartLD.getDayOfMonth();
                int currentHourStartMonth = currentHourStartLD.getMonthValue();
                int currentHourStartYear = currentHourStartLD.getYear();

                if(holidayDayOfMonth == currentHourStartDayOfMonth &&
                        holidayMonth == currentHourStartMonth &&
                        holidayYear == currentHourStartYear) {
                    coeff = 0.75;
                    break;
                }
            }
            totalPrice += ((double) pricePerHour * coeff);
            currentHourStart += (1000 * 60 * 60);
        }
        setTotalPrice(Math.round(totalPrice * 100) / 100d);
    }
    public void endReservation(Integer pricePerHour, List<Object> holidaysObjects) {
        this.endDate = new Date();
        countTotalPrice(pricePerHour, holidaysObjects);
    }

    public Car getCar() {
        return car;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId=" + reservationId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", car=" + car +
                ", parkingSpot=" + parkingSpot +
                ", totalPrice=" + totalPrice +
                '}';
    }
}