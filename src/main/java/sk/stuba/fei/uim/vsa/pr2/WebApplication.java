package sk.stuba.fei.uim.vsa.pr2;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import sk.stuba.fei.uim.vsa.pr2.resources.*;
import sk.stuba.fei.uim.vsa.pr2.service.CarParkService;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class WebApplication extends Application {
    public static final Set<Class<?>> appClasses = new HashSet<>();

    static {
        appClasses.add(CarParkResource.class);
        appClasses.add(CarResource.class);
        appClasses.add(HolidayResource.class);
        appClasses.add(ReservationResource.class);
        appClasses.add(UserResource.class);
    }

    public static final CarParkService CARPARKSERVICE = new CarParkService();


    @Override
    public Set<Class<?>> getClasses() { return appClasses; }
}
