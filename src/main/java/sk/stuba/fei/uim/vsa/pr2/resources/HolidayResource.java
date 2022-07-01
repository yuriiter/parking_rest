package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.WebApplication;
import sk.stuba.fei.uim.vsa.pr2.domain.Holiday;
import sk.stuba.fei.uim.vsa.pr2.dto.HolidayDto;
import sk.stuba.fei.uim.vsa.pr2.requests.HolidayRequest;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Path("/holidays")
public class HolidayResource {

    private final CarParkService carParkService = WebApplication.CARPARKSERVICE;
    private final ObjectMapper json = new ObjectMapper();


    @GET
    @Path("/{date}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("date") String date) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(date == null) {
            List<Object> resultsObject = carParkService.getHolidays();
            if(resultsObject == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            List<HolidayDto> results = resultsObject
                    .stream()
                    .map(x -> new HolidayDto((Holiday) x))
                    .collect(Collectors.toList());
            return Response.status(Response.Status.OK).entity(results).build();
        }
        else {
            try {
                Date inputDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                List<Object> result = carParkService.getHolidays(inputDate);
                if(result == null) {
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                List<HolidayDto> holidayDtos = result
                        .stream().map(x -> new HolidayDto((Holiday) x)).collect(Collectors.toList());
                return Response.status(Response.Status.OK).entity(holidayDtos).build();
            } catch (ParseException e) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        HolidayRequest hr;
        try{
            hr = json.readValue(body, HolidayRequest.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String name = hr.getName();
        String date = hr.getDate();

        Date inputDate;
        try {
            inputDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        }
        catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Object result = carParkService.createHoliday(name, inputDate);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.CREATED).entity(new HolidayDto((Holiday) result)).build();
    }


    @DELETE
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Object result = carParkService.deleteHoliday(id);
        if (result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
