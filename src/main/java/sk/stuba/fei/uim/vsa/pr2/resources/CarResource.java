package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.WebApplication;
import sk.stuba.fei.uim.vsa.pr2.domain.Car;
import sk.stuba.fei.uim.vsa.pr2.domain.CarPark;
import sk.stuba.fei.uim.vsa.pr2.domain.Reservation;
import sk.stuba.fei.uim.vsa.pr2.domain.User;
import sk.stuba.fei.uim.vsa.pr2.dto.CarDto;
import sk.stuba.fei.uim.vsa.pr2.dto.CarParkDto;
import sk.stuba.fei.uim.vsa.pr2.requests.CarRequest;
import sk.stuba.fei.uim.vsa.pr2.requests.UserRequest;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/cars")
public class CarResource {
    private final CarParkService carParkService = WebApplication.CARPARKSERVICE;
    private final ObjectMapper json = new ObjectMapper();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("user") Long userId, @QueryParam("vrp") String vrp) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        List<Car> cars = carParkService
                .getCars()
                .stream().map(o -> (Car) o)
                .collect(Collectors.toList());
        List<CarDto> carDtos = cars
                .stream()
                .filter(c ->
                        (
                                (userId == null || c.getUser().getUserId().equals(userId)) &&
                                        (vrp == null || vrp.equals(c.getVehicleRegistrationPlate())))
                )
                .map(CarDto::new)
                .collect(Collectors.toList());
        for(CarDto carDto : carDtos) {
            List<Reservation> reservations = carParkService.getReservations(carDto.getId(), new Date())
                    .stream().map(o -> (Reservation) o).collect(Collectors.toList());
            List<Long> reservationsLong = reservations
                    .stream().map(Reservation::getReservationId).collect(Collectors.toList());
            carDto.setReservations(reservationsLong);
        }
        return Response.status(Response.Status.OK).entity(carDtos).build();
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Object carObject = carParkService
                .getCars().stream().filter(o -> ((Car) o).getCarId().equals(id)).findFirst().orElse(null);
        if(carObject == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Car car = (Car) carObject;
        CarDto carDto = new CarDto(car);

        List<Reservation> reservations = carParkService.getReservations(carDto.getId(), new Date())
                .stream().map(o -> (Reservation) o).collect(Collectors.toList());
        List<Long> reservationsLong = reservations
                .stream().map(Reservation::getReservationId).collect(Collectors.toList());
        carDto.setReservations(reservationsLong);

        return Response.status(Response.Status.OK).entity(carDto).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        CarRequest cr;
        try{
            cr = json.readValue(body, CarRequest.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        UserRequest ur = cr.getOwner();
        Object userResult = carParkService.createUser(ur.getFirstName(), ur.getLastName(), ur.getEmail());
        if(userResult == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Long userId = ((User) userResult).getUserId();
        String brand = cr.getBrand();
        String model = cr.getModel();
        String colour = cr.getColour();
        String vehicleRegistrationPlate = cr.getVrp();

        Object result = carParkService.createCar(userId, brand, model, colour, vehicleRegistrationPlate);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).entity(new CarParkDto((CarPark) result)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        CarDto carDto;
        try{
            carDto = json.readValue(body, CarDto.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Long userId = carDto.getOwner();
        String brand = carDto.getBrand();
        String model = carDto.getModel();
        String colour = carDto.getColour();
        String vehicleRegistrationPlate = carDto.getVrp();

        Car car = new Car(brand, model, colour, vehicleRegistrationPlate);
        car.setCarId(id);

        Object result = carParkService.updateCar(car);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity(new CarParkDto((CarPark) result)).build();
    }



    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Object result = carParkService.deleteCar(id);
        if (result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}