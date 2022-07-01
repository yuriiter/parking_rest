package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.WebApplication;
import sk.stuba.fei.uim.vsa.pr2.domain.CarPark;
import sk.stuba.fei.uim.vsa.pr2.domain.CarParkFloor;
import sk.stuba.fei.uim.vsa.pr2.domain.ParkingSpot;
import sk.stuba.fei.uim.vsa.pr2.domain.Reservation;
import sk.stuba.fei.uim.vsa.pr2.dto.ParkingSpotDto;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;

import java.util.*;
import java.util.stream.Collectors;

public class ParkingSpotResource {
    private final CarParkService carParkService = WebApplication.CARPARKSERVICE;
    private final ObjectMapper json = new ObjectMapper();

    @GET
    @Path("/carparks/{id}/spots")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long carParkId, @QueryParam("free") Boolean free) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(carParkId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Map<String, List<Object>> result = carParkService.getParkingSpots(carParkId);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<ParkingSpotDto> responseArray = new ArrayList<>();
        for(Map.Entry<String, List<Object>> entry : result.entrySet()) {
            List<ParkingSpotDto> newList = entry.getValue()
                    .stream().map(o -> new ParkingSpotDto(
                            ((ParkingSpot) o).getParkingSpotId(),
                            ((ParkingSpot) o).getSpotIdentifier(),
                            entry.getKey(),
                            carParkId,
                            ((ParkingSpot) o).getCar() == null,
                            carParkService.getReservations(((ParkingSpot) o).getParkingSpotId(), new Date())
                                    .stream().map(ro -> ((Reservation) ro).getReservationId())
                                    .collect(Collectors.toList())
                    )).collect(Collectors.toList());
            responseArray.addAll(newList);
        }


        if(free != null){
            if(free) {
                responseArray.stream().filter(rp -> rp.getFree()).collect(Collectors.toList());
            }
            else {
                responseArray.stream().filter(rp -> !rp.getFree()).collect(Collectors.toList());
            }
        }

        return Response.status(Response.Status.OK).entity(responseArray).build();
    }



    @GET
    @Path("/carparks/{id}/floors/{identifier}/spots")
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
        List<Object> result = carParkService.getParkingSpots(id, identifier);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        List<ParkingSpotDto> arrayResponse = result
                .stream().map(o -> new ParkingSpotDto(
                        ((ParkingSpot) o).getParkingSpotId(),
                        ((ParkingSpot) o).getSpotIdentifier(),
                        identifier,
                        id,
                        ((ParkingSpot) o).getCar() == null,
                        carParkService.getReservations(((ParkingSpot) o).getParkingSpotId(), new Date())
                                .stream().map(ro -> ((Reservation) ro).getReservationId())
                                .collect(Collectors.toList())
                )).collect(Collectors.toList());
        return Response.status(Response.Status.OK).entity(arrayResponse).build();
    }


    @GET
    @Path("/parkingspots/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Object result = carParkService.getParkingSpot(id);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        ParkingSpot resultConverted = (ParkingSpot) result;
        Long parkingSpotId = resultConverted.getParkingSpotId();
        List<CarPark> carParks = carParkService.getCarParks()
                .stream().map(o -> (CarPark) o).collect(Collectors.toList());
        String carParkFloorIdentifier = null;
        Long carParkId = null;
        for(CarPark carPark : carParks) {
            boolean loop1 = true;
            List<CarParkFloor> carParkFloors = carPark.getCarParkFloors();
            for(CarParkFloor carParkFloor : carParkFloors) {
                List<ParkingSpot> parkingSpots = carParkFloor.getParkingSpots();
                boolean loop2 = true;
                for(ParkingSpot parkingSpot : parkingSpots) {
                    if(parkingSpot.getParkingSpotId().equals(parkingSpotId)) {
                        carParkFloorIdentifier = carParkFloor.getCarParkFloorPK().getFloorIdentifier();
                        carParkId = carPark.getCarParkId();
                        loop1 = false;
                        loop2 = false;
                        break;
                    }
                }
                if(!loop2) {
                    break;
                }
            }
            if(!loop1) {
                break;
            }
        }
        if(carParkFloorIdentifier == null || carParkId == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ParkingSpotDto response = new ParkingSpotDto(
                id,
                resultConverted.getSpotIdentifier(),
                carParkFloorIdentifier,
                carParkId,
                resultConverted.getCar() == null,
                carParkService.getReservations(resultConverted.getParkingSpotId(), new Date())
                        .stream().map(ro -> ((Reservation) ro).getReservationId())
                        .collect(Collectors.toList())
        );
        return Response.status(Response.Status.CREATED).entity(response).build();
    }



    @POST
    @Path("/carparks/{id}/floors/{identifier}/spots")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id,
                           @PathParam("identifier") String identifier, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        ParkingSpotDto parkingSpotDto;
        try{
            parkingSpotDto = json.readValue(body, ParkingSpotDto.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Long carParkId = parkingSpotDto.getCarPark();
        String floorIdentifier = parkingSpotDto.getCarParkFloor();
        String spotIdentifier = parkingSpotDto.getIdentifier();

        Object result = carParkService
                .createParkingSpot(carParkId, floorIdentifier, spotIdentifier);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        Long parkingSpotId = ((ParkingSpot) result).getParkingSpotId();
        parkingSpotDto.setId(parkingSpotId);
        parkingSpotDto.setReservations(new ArrayList<>());
        parkingSpotDto.setFree(true);

        return Response.status(Response.Status.CREATED).entity(parkingSpotDto).build();
    }

    @DELETE
    @Path("/parkingspots/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(id == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Object result = carParkService.deleteParkingSpot(id);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }


}
