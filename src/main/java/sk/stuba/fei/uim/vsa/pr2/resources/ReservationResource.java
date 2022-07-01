package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.WebApplication;
import sk.stuba.fei.uim.vsa.pr2.domain.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.domain.Reservation;
import sk.stuba.fei.uim.vsa.pr2.domain.User;
import sk.stuba.fei.uim.vsa.pr2.dto.ReservationDto;
import sk.stuba.fei.uim.vsa.pr2.requests.CarRequest;
import sk.stuba.fei.uim.vsa.pr2.requests.ParkingSpotRequest;
import sk.stuba.fei.uim.vsa.pr2.requests.ReservationRequest;
import sk.stuba.fei.uim.vsa.pr2.requests.UserRequest;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/reservations")
public class ReservationResource {

    private final CarParkService carParkService = WebApplication.CARPARKSERVICE;
    private final ObjectMapper json = new ObjectMapper();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("user") Long user, @QueryParam("spot") Long spot, @QueryParam("date") String date) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(date == null ^ spot == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(date != null && spot != null && user != null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else if(date != null && spot != null) {
            try {
                Date inputDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                List<Object> result = carParkService.getReservations(spot, inputDate);
                if(result == null) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                List<ReservationDto> reservationDtos = result
                        .stream().map(o -> new ReservationDto((Reservation) o)).collect(Collectors.toList());
                return Response.status(Response.Status.OK).entity(reservationDtos).build();
            } catch (ParseException e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
        else if(user != null) {
            List<Object> result = carParkService.getMyReservations(user);
            if(result == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            List<ReservationDto> reservationDtos = result
                    .stream().map(o -> new ReservationDto((Reservation) o)).collect(Collectors.toList());
            return Response.status(Response.Status.OK).entity(reservationDtos).build();
        }
        else {
            List<Object> result = carParkService.getAllReservations();
            if(result == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            List<ReservationDto> reservationDtos = result
                    .stream().map(o -> new ReservationDto((Reservation) o)).collect(Collectors.toList());
            return Response.status(Response.Status.OK).entity(reservationDtos).build();
        }
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Object result = carParkService.getReservation(id);
        if(result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        ReservationDto reservationDto = new ReservationDto((Reservation) result);
        return Response.status(Response.Status.OK).entity(reservationDto).build();
    }


    @POST
    @Path("/{id}/end")
    @Produces(MediaType.APPLICATION_JSON)
    public Response end(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Object reservationObject = carParkService.getReservation(id);
        if(reservationObject == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Long reservationUserId = ((Reservation) reservationObject).getCar().getUser().getUserId();
        String base64Encoded = authorization.substring("Basic ".length());
        String decoded = new String(Base64.getDecoder().decode(base64Encoded));
        String userId = decoded.split(":")[1];

        if(!userId.equals(reservationUserId.toString())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Object result = carParkService.endReservation(id);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).entity(new ReservationDto((Reservation) result)).build();
    }



    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        ReservationRequest rr;
        try{
            rr = json.readValue(body, ReservationRequest.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ParkingSpotRequest psr = rr.getSpot();
        CarRequest cr = rr.getCar();
        UserRequest ur = cr.getOwner();
        Object userResult = carParkService.createUser(ur.getFirstName(), ur.getLastName(), ur.getEmail());
        if(userResult == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Object carResult = carParkService.createCar(((User) userResult).getUserId(), cr.getBrand(), cr.getModel(), cr.getColour(), cr.getVrp());
        if(carResult == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Object psrResult = carParkService.createParkingSpot(psr.getCarPark(), psr.getCarParkFloor(), psr.getIdentifier());
        Object result = carParkService.createReservation(((ParkingSpot) psrResult).getParkingSpotId(), cr.getId());
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).entity(new ReservationDto((Reservation) result)).build();
    }

}
