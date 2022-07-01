package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.WebApplication;
import sk.stuba.fei.uim.vsa.pr2.domain.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.dto.CarParkFloorDto;
import sk.stuba.fei.uim.vsa.pr2.requests.*;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;

import java.util.List;
import java.util.stream.Collectors;

public class CarParkFloorResource {
    private final CarParkService carParkService = WebApplication.CARPARKSERVICE;
    private final ObjectMapper json = new ObjectMapper();

    @GET
    @Path("/carparks/{id}/floors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long carParkId) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(carParkId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<Object> resultsObject = carParkService.getCarParkFloors(carParkId);
        if(resultsObject == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        List<CarParkFloorDto> results = resultsObject
                .stream()
                .map(x -> (CarParkFloorDto) x)
                .collect(Collectors.toList());
        return Response.status(Response.Status.OK).entity(results).build();
    }


    @GET
    @Path("/carparks/{id}/floors/{identifier}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(identifier == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(identifier.length() == 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Object result = carParkService.getCarParkFloor(id, identifier);
        if(result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).entity(new CarParkFloorDto((CarParkFloor) result)).build();
    }


    @POST
    @Path("/carparks/{id}/floors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        CarParkFloorRequest cpfr;
        try{
            cpfr = json.readValue(body, CarParkFloorRequest.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Long carParkId = cpfr.getCarPark();
        String identifier = cpfr.getIdentifier();

        Object result = carParkService.createCarParkFloor(carParkId, identifier);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        for(ParkingSpotRequest psr : cpfr.getSpots()) {
            carParkService.createParkingSpot(carParkId, identifier, psr.getIdentifier());
        }
        result = carParkService.merge(result);
        return Response.status(Response.Status.CREATED).entity(new CarParkFloorDto((CarParkFloor) result)).build();
    }

    @DELETE
    @Path("/carparks/{id}/floors/{identifier}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, @PathParam("identifier") String identifier) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(identifier == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        if(id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Object result = carParkService.deleteCarParkFloor(id, identifier);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

//    @PUT
//    @Path("/carparks/{id}/floors/{identifier}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response update(@PathParam("id") Long id, @PathParam("identifier"), String body) {
//        if(id == null) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//        CarParkFloorDto carParkFloorDto;
//        try{
//            carParkFloorDto = json.readValue(body, CarParkFloorDto.class);
//        } catch (JsonProcessingException e){
//            System.err.println(e.getMessage());
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//        Long carParkId = carParkFloorDto.getCarPark();
//        String identifier = carParkFloorDto.getIdentifier();
//
//        CarParkFloor carParkFloor = new CarParkFloor();
//        carParkFloor.set
//        carPark.setCarParkId(id);
//
//        Object result = carParkService.updateCarPark(carPark);
//        if(result == null) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//        return Response.status(Response.Status.OK).entity(new CarParkDto((CarPark) result)).build();
//    }

}
