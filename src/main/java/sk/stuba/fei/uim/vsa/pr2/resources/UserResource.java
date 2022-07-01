package sk.stuba.fei.uim.vsa.pr2.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import sk.stuba.fei.uim.vsa.pr2.WebApplication;
import sk.stuba.fei.uim.vsa.pr2.domain.Car;
import sk.stuba.fei.uim.vsa.pr2.domain.User;
import sk.stuba.fei.uim.vsa.pr2.dto.UserDto;
import sk.stuba.fei.uim.vsa.pr2.requests.CarRequest;
import sk.stuba.fei.uim.vsa.pr2.requests.UserRequest;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/users")
public class UserResource {

    private final CarParkService carParkService = WebApplication.CARPARKSERVICE;
    private final ObjectMapper json = new ObjectMapper();


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @QueryParam("email") String email) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if(email != null) {
            Object result = carParkService.getUser(email);
            if(result == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            UserDto userDto = new UserDto((User) result);
            List<Object> carObjects = carParkService.getCars(userDto.getId());
            userDto.setCars(carObjects
                    .stream().map(o -> ((Car) o).getCarId())
                    .collect(Collectors.toList()));
            return Response.status(Response.Status.OK).entity(userDto).build();
        }

        List<User> users = carParkService
                .getUsers()
                .stream().map(o -> (User) o)
                .collect(Collectors.toList());
        List<UserDto> userDtos = new ArrayList<>();
        for(User user : users) {
            UserDto userDto = new UserDto(user);
            List<Object> carObjects = carParkService.getCars(userDto.getId());
            userDto.setCars(carObjects
                    .stream().map(o -> ((Car) o).getCarId())
                    .collect(Collectors.toList()));
        }
        return Response.status(Response.Status.OK).entity(userDtos).build();
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
        Object result = carParkService.getUser(id);
        if(result == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        UserDto userDto = new UserDto((User) result);
        List<Object> carObjects = carParkService.getCars(userDto.getId());
        userDto.setCars(carObjects
                .stream().map(o -> ((Car) o).getCarId())
                .collect(Collectors.toList()));
        return Response.status(Response.Status.OK).entity(userDto).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        UserRequest ur;
        try{
            ur = json.readValue(body, UserRequest.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String firstname = ur.getFirstName();
        String lastname = ur.getLastName();
        String email = ur.getEmail();

        Object result = carParkService.createUser(firstname, lastname, email);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        List<CarRequest> carRequests = ur.getCars();
        for(CarRequest cr : carRequests) {
            carParkService.createCar(((User) result).getUserId(), cr.getBrand(), cr.getModel(), cr.getColour(), cr.getVrp());
        }
        result = carParkService.merge(result);
        return Response.status(Response.Status.CREATED).entity(new UserDto((User) result)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id, String body) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        UserDto userDto;
        try{
            userDto = json.readValue(body, UserDto.class);
        } catch (JsonProcessingException e){
            System.err.println(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String firstname = userDto.getFirstName();
        String lastname = userDto.getLastName();
        String email = userDto.getEmail();

        User user = new User(firstname, lastname, email);
        user.setUserId(id);

        Object result = carParkService.updateUser(user);
        if(result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.OK).entity(new UserDto((User) result)).build();
    }



    @DELETE
    @Path("/{id}")
    public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String authorization, @PathParam("id") Long id) {
        if(!carParkService.authorize(authorization)) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        Object result = carParkService.deleteUser(id);
        if (result == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

}
