package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.WebApplication;
import sk.stuba.fei.uim.vsa.pr2.dto.CarParkDto;
import sk.stuba.fei.uim.vsa.pr2.requests.CarParkFloorRequest;
import sk.stuba.fei.uim.vsa.pr2.requests.CarParkRequest;
import sk.stuba.fei.uim.vsa.pr2.requests.ParkingSpotRequest;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;
import sk.stuba.fei.uim.vsa.pr2.domain.CarPark;

import java.util.List;
import java.util.stream.Collectors;

@Path("/carparks")
public class CarParkResource {
    private final CarParkService carParkService = WebApplication.CARPARKSERVICE;

    private final ObjectMapper json = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("name") String name) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(name == null) {
            List<Object> resultsObject = carParkService.getCarParks();
            if(resultsObject == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            List<CarParkDto> results = resultsObject
                    .stream()
                    .map(x -> new CarParkDto((CarPark) x))
                    .collect(Collectors.toList());
            return Response.status(Response.Status.OK).entity(results).build();
        }
        else {
            Object result = carParkService.getCarPark(name);
            if(result == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.OK).entity(new CarParkDto((CarPark) result)).build();
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
        Object result = carParkService.getCarPark(id);
        if(result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(new CarParkDto((CarPark) result)).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        CarParkRequest cpr;
        System.out.println(body);
        try{
            cpr = json.readValue(body, CarParkRequest.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String name = cpr.getName();
        String address = cpr.getAddress();
        Integer pricePerHour = cpr.getPrices();
        Object result = carParkService.createCarPark(name, address, pricePerHour);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<CarParkFloorRequest> carParkFloorRequests = cpr.getFloors();
        for(CarParkFloorRequest cpfr : carParkFloorRequests) {
            carParkService.createCarParkFloor(((CarPark) result).getCarParkId(), cpfr.getIdentifier());
            List<ParkingSpotRequest> parkingSpotRequests = cpfr.getSpots();
            for(ParkingSpotRequest psr : parkingSpotRequests) {
                carParkService.createParkingSpot(((CarPark) result).getCarParkId(), psr.getCarParkFloor(), psr.getIdentifier());
            }
        }
        result = carParkService.merge(result);
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
        CarParkDto carParkDto;
        try{
            carParkDto = json.readValue(body, CarParkDto.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String name = carParkDto.getName();
        String address = carParkDto.getAddress();
        Integer pricePerHour = carParkDto.getPricePerHour();
        carParkDto.setId(id);

        CarPark carPark = new CarPark(name, address, pricePerHour);
        carPark.setCarParkId(id);

        Object result = carParkService.updateCarPark(carPark);
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
        Object result = carParkService.deleteCarPark(id);
        if (result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
