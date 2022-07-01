package sk.stuba.fei.uim.vsa.pr2.service;

import sk.stuba.fei.uim.vsa.pr2.domain.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class CarParkService extends AbstractCarParkService {
    private static boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }
    /**
     * Vytvorenie nového parkovacieho domu
     *
     * @param name         názov parkovacieho domu
     * @param address      adresa parkovacieho domu
     * @param pricePerHour cena za hodinu parkovania
     * @return objekt entity parkovacieho domu
     */
    @Override
    public Object createCarPark(String name, String address, Integer pricePerHour) {
        if(name == null || address == null || pricePerHour == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        TypedQuery<CarPark> query = em.createQuery("SELECT cp FROM CarPark cp WHERE cp.name = ?1", CarPark.class);
        try {
            query.setParameter(1, name).getSingleResult();
        }
        catch(Exception e) {
            CarPark carPark = new CarPark(name, address, pricePerHour);
            transaction.begin();
            em.persist(carPark);
            transaction.commit();
            em.close();
            return carPark;
        }
        em.close();
        return null;
    }

    /**
     * Získanie entity parkovacieho domu podľa ID
     *
     * @param carParkId id parkovacieho domu
     * @return objekt entity parkovacieho domu
     */
    @Override
    public Object getCarPark(Long carParkId) {
        if(carParkId == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        CarPark carPark = em.find(CarPark.class, carParkId);
        em.close();
        return carPark;
    }

    /**
     * Získanie entity parkovacieho domu podľa názvu domu
     *
     * @param carParkName názov parkovacieho domu
     * @return objekt entity parkovacieho domu
     */
    @Override
    public Object getCarPark(String carParkName) {
        if(carParkName == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<CarPark> query = em.createQuery("SELECT cp FROM CarPark cp WHERE cp.name = ?1", CarPark.class);
        CarPark carPark;
        try {
            carPark = query.setParameter(1, carParkName).getSingleResult();
        }
        catch(Exception e) {
            em.close();
            return null;
        }
        em.close();
        return carPark;
    }

    /**
     * Získanie zoznamu všetkých parkovacích domov
     *
     * @return zoznam entít parkovacích domov
     */
    @Override
    public List<Object> getCarParks() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<CarPark> query = em.createQuery("SELECT cp FROM CarPark cp", CarPark.class);
        List<Object> resultList;
        try {
            resultList = new ArrayList<>(query.getResultList());
        }
        catch(Exception e) {
            em.close();
            return null;
        }
        em.close();
        return resultList;
    }

    @Override
    public Object updateCarPark(Object carPark) {
        if(carPark == null) {
            return null;
        }
        if(!(carPark instanceof CarPark)) {
            return null;
        }
        CarPark newCarPark = (CarPark) carPark;
        if(newCarPark.getCarParkId() == null
                || newCarPark.getName() == null
                || newCarPark.getAddress() == null
        ) {
            return null;
        }
        CarPark updatedCarPark = (CarPark) getCarPark(newCarPark.getCarParkId());
        if(updatedCarPark == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        if(!updatedCarPark.getName().equals(newCarPark.getName())) {
            TypedQuery<CarPark> query = em.createQuery("SELECT cp FROM CarPark cp WHERE cp.name = ?1", CarPark.class);
            try {
                query.setParameter(1, newCarPark.getName()).getSingleResult();
            }
            catch(Exception e) {
                updatedCarPark.copyCarParkWithoutFloors(newCarPark);
                em.getTransaction().begin();
                em.merge(updatedCarPark);
                em.getTransaction().commit();
                em.close();
                return updatedCarPark;
            }
        }
        else {
            updatedCarPark.copyCarParkWithoutFloors(newCarPark);
            em.getTransaction().begin();
            em.merge(updatedCarPark);
            em.getTransaction().commit();
            em.close();
            return updatedCarPark;
        }

        em.close();
        return null;
    }

    /**
     * Vymazanie parkovacieho domu podľa id
     *
     * @param carParkId id parkovacieho domu
     * @return objekt vymazaného parkovacieho domu
     */
    @Override
    public Object deleteCarPark(Long carParkId) {
        if(carParkId == null) {
            return null;
        }
        Object carPark = getCarPark(carParkId);
        if(carPark == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        TypedQuery<CarPark> query = em
                .createQuery("DELETE FROM CarPark cp WHERE cp.carParkId = ?1", CarPark.class)
                .setParameter(1, carParkId);
        query.executeUpdate();
        em.getTransaction().commit();
        em.close();
        return carPark;
    }


    // Poschodia parkovacieho domu

    /**
     * Vytvorenie poschodia parkovacieho domu
     *
     * @param carParkId       id parkovacieho domu
     * @param floorIdentifier identifikátor poschodia. Môže byť číslo podlažia, alebo iná skratka pre poschodie.
     *                        Musí byť unikátna v rámci parkovacieho domu.
     * @return objekt entity poschodia
     */
    @Override
    public Object createCarParkFloor(Long carParkId, String floorIdentifier) {
        if(carParkId == null) {
            return null;
        }
        if(floorIdentifier == null) {
            return null;
        }
        CarPark carPark = (CarPark) getCarPark(carParkId);
        if(carPark == null) {
            return null;
        }
        Optional<CarParkFloor> result = carPark.getCarParkFloors().stream()
                .filter(carParkFloorItem -> floorIdentifier.equals(carParkFloorItem.getFloorIdentifier()))
                .findFirst();
        if(result.isPresent()) {
            return null;
        }

        CarParkFloor carParkFloor = new CarParkFloor(floorIdentifier, carPark);
        carPark.addCarParkFloor(carParkFloor);
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.merge(carPark);
        em.persist(carParkFloor);
        transaction.commit();
        em.close();
        return carParkFloor;
    }

    /**
     * Získanie entity poschodia parkovacieho domu
     *
     * @param carParkId       id parkovacieho domu
     * @param floorIdentifier identifikátor poschodia
     * @return objekt entity poschodia
     */
    @Override
    public Object getCarParkFloor(Long carParkId, String floorIdentifier) {
        if(carParkId == null || floorIdentifier == null) {
            return null;
        }
        if(floorIdentifier.length() == 0) {
            return null;
        }
        CarPark carPark = (CarPark) getCarPark(carParkId);
        if(carPark == null) {
            return null;
        }
        List<CarParkFloor> carParkFloors = carPark.getCarParkFloors();
        Optional<CarParkFloor> carParkFloorOptional = carParkFloors.stream()
                .filter(carParkFloorItem -> floorIdentifier.equals(carParkFloorItem.getFloorIdentifier()))
                .findFirst();
        return carParkFloorOptional.orElse(null);
    }

    @Override
    public Object getCarParkFloor(Long carParkFloorId) {
        return null;
    }

    /**
     * Získanie zoznamu entít všetkých poschodí v parkovacom dome
     *
     * @param carParkId id parkovacieho domu
     * @return zoznam entít poschodí
     */
    @Override
    public List<Object> getCarParkFloors(Long carParkId) {
        if(carParkId == null) {
            return null;
        }
        CarPark carPark = (CarPark) getCarPark(carParkId);
        if(carPark == null) {
            return null;
        }
        List<CarParkFloor> carParkFloors = carPark.getCarParkFloors();
        if(carParkFloors == null) {
            return null;
        }
        return carParkFloors.stream()
                .map(carParkFloorsItem -> (Object) carParkFloorsItem)
                .collect(Collectors.toList());
    }

    @Override
    public Object updateCarParkFloor(Object carParkFloor) {
        return null;
    }

    /**
     * Vymazanie poschodia v parkovacom dome
     *
     * @param carParkId       id parkovacieho domu
     * @param floorIdentifier identifikátor poschodia
     * @return vymazaná entita poschodia
     */
    @Override
    public Object deleteCarParkFloor(Long carParkId, String floorIdentifier) {
        if(carParkId == null || floorIdentifier == null) {
            return null;
        }
        if(floorIdentifier.length() == 0) {
            return null;
        }
        Object carParkFloor = getCarParkFloor(carParkId, floorIdentifier);
        if(carParkFloor == null) {
            return null;
        }
        CarPark carPark = (CarPark) getCarPark(carParkId);
        carPark.removeParkFloor((CarParkFloor) carParkFloor);
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.merge(carPark);
        if (!em.contains(carParkFloor)) {
            carParkFloor = em.merge(carParkFloor);
        }
        em.remove(carParkFloor);
        em.getTransaction().commit();
        em.close();
        return carParkFloor;
    }

    @Override
    public Object deleteCarParkFloor(Long carParkFloorId) {
        return null;
    }


    // Parkovacie miesto

    /**
     * Vytvorenie parkovacieho miesta na poschodí parkovacieho domu
     *
     * @param carParkId       id parkovacieho domu
     * @param floorIdentifier identifikátor poschodia
     * @param spotIdentifier  identifikátor parkovacieho miesta. Môže byť poradové číslo, alebo iná skratka pre označenie miesta.
     *                        Musí byť unikátna v rámci parkovacieho domu.
     * @return objekt entity parkovacieho miesta
     */
    @Override
    public Object createParkingSpot(Long carParkId, String floorIdentifier, String spotIdentifier) {
        if(carParkId == null || floorIdentifier == null || spotIdentifier == null) {
            return null;
        }
        Object carParkFloorObject = getCarParkFloor(carParkId, floorIdentifier);
        if(carParkFloorObject == null) {
            return null;
        }
        List<Object> carParkFloorsObjects = getCarParkFloors(carParkId);
        for(Object carParkFloorsObject : carParkFloorsObjects) {
            CarParkFloor carParkFloor_ = (CarParkFloor) carParkFloorsObject;
            Optional<ParkingSpot> parkingSpotOptional = carParkFloor_.getParkingSpots()
                    .stream().filter(parkingSpot -> parkingSpot.getSpotIdentifier().equals(spotIdentifier))
                    .findFirst();
            if(parkingSpotOptional.isPresent()) {
                return null;
            }
        }
        EntityManager em = emf.createEntityManager();
        ParkingSpot parkingSpot = new ParkingSpot(spotIdentifier);
        CarParkFloor carParkFloor = (CarParkFloor) carParkFloorObject;
        carParkFloor.addParkingSpot(parkingSpot);
        em.getTransaction().begin();
        em.persist(parkingSpot);
        em.merge(carParkFloor);
        em.getTransaction().commit();
        em.close();
        return parkingSpot;
    }

    /**
     * Získanie parkovacieho miesta
     *
     * @param parkingSpotId id parkovacieho miesta
     * @return objekt entity parkovacieho miesta
     */
    @Override
    public Object getParkingSpot(Long parkingSpotId) {
        if(parkingSpotId == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        return em.find(ParkingSpot.class, parkingSpotId);
    }

    /**
     * Získanie zoznamu parkovacích miest na poschodí parkovacieho domu
     *
     * @param carParkId       id parkovacieho domu
     * @param floorIdentifier identifikátor poschodia
     * @return object entity parkovacieho miesta
     */
    @Override
    public List<Object> getParkingSpots(Long carParkId, String floorIdentifier) {
        if(carParkId == null || floorIdentifier == null) {
            return null;
        }
        CarParkFloor carParkFloor = (CarParkFloor) getCarParkFloor(carParkId, floorIdentifier);
        if(carParkFloor == null) {
            return null;
        }
        List<ParkingSpot> parkingSpots = carParkFloor.getParkingSpots();
        if(parkingSpots == null) {
            return null;
        }
        return parkingSpots.stream()
                .map(parkingSpot -> (Object) parkingSpot)
                .collect(Collectors.toList());
    }

    /**
     * Získanie zoznamu parkovacích miest v parkovacom dome
     *
     * @param carParkId id parkovacieho domu
     * @return zoznam parkovacích miest. Kľúč mapy je identifikátor poschodia a hodnota je zoznam parkovacích miest na danom poschodí.
     */
    @Override
    public Map<String, List<Object>> getParkingSpots(Long carParkId) {
        if(carParkId == null) {
            return null;
        }
        CarPark carPark = (CarPark) getCarPark(carParkId);
        if(carPark == null) {
            return null;
        }
        List<Object> carParkFloorsObjects = getCarParkFloors(carParkId);
        if(carParkFloorsObjects == null) {
            return null;
        }
        List<CarParkFloor> carParkFloors = carParkFloorsObjects.stream()
                .map(carParkFloorObject -> (CarParkFloor) carParkFloorObject)
                .collect(Collectors.toList());
        Map<String, List<Object>> floorIdentifierToParkingSpots = new HashMap<>();
        for(CarParkFloor carParkFloor : carParkFloors) {
            List<ParkingSpot> parkingSpots = carParkFloor.getParkingSpots();
            List<Object> parkingSpotsObjects;
            if(parkingSpots == null) {
                parkingSpotsObjects = null;
            }
            else {
                parkingSpotsObjects = new ArrayList<>(parkingSpots);
            }
            floorIdentifierToParkingSpots.put(carParkFloor.getFloorIdentifier(), parkingSpotsObjects);
        }
        return floorIdentifierToParkingSpots;
    }

    /**
     * Získanie zoznamu parkovacích miest, ktoré sú dostupné, t.j. nie je na nich zaparkované auto.
     *
     * @param carParkName názov parkovacieho domu
     * @return zoznam parkovacích miest. Kľúč mapy je identifikátor poschodia a hodnota je zoznam voľných parkovacích miest na danom poschodí.
     */
    @Override
    public Map<String, List<Object>> getAvailableParkingSpots(String carParkName) {
        if(carParkName == null) {
            return null;
        }
        CarPark carPark = (CarPark) getCarPark(carParkName);
        if(carPark == null) {
            return null;
        }
        Map<String, List<Object>> floorIdentifierToParkingSpots = getParkingSpots(carPark.getCarParkId());
        if(floorIdentifierToParkingSpots == null) {
            return null;
        }
        Map<String, List<Object>> floorIdentifierToAvailableParkingSpots = new HashMap<>();
        for(Map.Entry<String, List<Object>> entry : floorIdentifierToParkingSpots.entrySet()) {
            List<Object> parkingSpotsObjects = entry.getValue();
            List<Object> availableParkingSpotsObjects;
            if(parkingSpotsObjects == null) {
                availableParkingSpotsObjects = null;
            }
            else {
                availableParkingSpotsObjects = parkingSpotsObjects.stream()
                        .map(parkingSpotsObject -> (ParkingSpot) parkingSpotsObject)
                        .filter(parkingSpot -> parkingSpot.getCar() == null)
                        .map(parkingSpot -> (Object) parkingSpot).collect(Collectors.toList());
            }
            floorIdentifierToAvailableParkingSpots.put(entry.getKey(), availableParkingSpotsObjects);
        }
        return floorIdentifierToAvailableParkingSpots;
    }

    /**
     * Získanie zoznamu parkovacích miest, ktoré sú obsadené, t.j. je na nich zaparkované auto.
     *
     * @param carParkName názov parkovacieho domu
     * @return zoznam parkovacích miest. Kľúč mapy je identifikátor poschodia a hodnota je zoznam obsadených parkovacích miest na danom poschodí.
     */
    @Override
    public Map<String, List<Object>> getOccupiedParkingSpots(String carParkName) {
        if(carParkName == null) {
            return null;
        }
        CarPark carPark = (CarPark) getCarPark(carParkName);
        if(carPark == null) {
            return null;
        }
        Map<String, List<Object>> floorIdentifierToParkingSpots = getParkingSpots(carPark.getCarParkId());
        if(floorIdentifierToParkingSpots == null) {
            return null;
        }
        Map<String, List<Object>> floorIdentifierToUnavailableParkingSpots = new HashMap<>();
        for(Map.Entry<String, List<Object>> entry : floorIdentifierToParkingSpots.entrySet()) {
            List<Object> parkingSpotsObjects = entry.getValue();
            List<Object> unavailableParkingSpotsObjects;
            if(parkingSpotsObjects == null) {
                unavailableParkingSpotsObjects = null;
            }
            else {
                unavailableParkingSpotsObjects = parkingSpotsObjects.stream()
                        .map(parkingSpotsObject -> (ParkingSpot) parkingSpotsObject)
                        .filter(parkingSpot -> parkingSpot.getCar() != null)
                        .map(parkingSpot -> (Object) parkingSpot).collect(Collectors.toList());
            }
            floorIdentifierToUnavailableParkingSpots.put(entry.getKey(), unavailableParkingSpotsObjects);
        }
        return floorIdentifierToUnavailableParkingSpots;
    }

    @Override
    public Object updateParkingSpot(Object parkingSpot) {
//        if(parkingSpot == null) {
//            return null;
//        }
//        if(!(parkingSpot instanceof ParkingSpot)) {
//            return null;
//        }
//        EntityManager em = emf.createEntityManager();
//        if(!em.contains(parkingSpot)) {
//            em.close();
//            return null;
//        }
//        em.getTransaction().begin();
//        em.merge(parkingSpot);
//        em.getTransaction().commit();
//        em.close();
        return null;
    }

    /**
     * Vymazanie parkovacieho miesta
     *
     * @param parkingSpotId id parkovacieho miesta
     * @return vymazané parkovacie miesto
     */
    @Override
    public Object deleteParkingSpot(Long parkingSpotId) {
        if(parkingSpotId == null) {
            return null;
        }
        Object parkingSpot = getParkingSpot(parkingSpotId);
        if(parkingSpot == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<CarParkFloor> query = em
                .createQuery("SELECT cpf FROM CarParkFloor cpf", CarParkFloor.class);
        List<CarParkFloor> carParkFloorList = query.getResultList();
        CarParkFloor parent = null;
        for(CarParkFloor carParkFloor : carParkFloorList) {
            List<ParkingSpot> parkingSpotList = carParkFloor.getParkingSpots();
            Optional<ParkingSpot> parkingSpotOptional =
                    parkingSpotList.stream()
                            .filter(parkingSpotFiltered -> parkingSpotFiltered.getParkingSpotId().equals(parkingSpotId))
                            .findAny();
            if(parkingSpotOptional.isPresent()) {
                parent = carParkFloor;
                break;
            }
        }
        if(parent == null) {
            em.close();
            return null;
        }
        parent.removeParkingSpot((ParkingSpot) parkingSpot);
        if(!em.contains(parkingSpot)) {
            parkingSpot = em.merge(parkingSpot);
        }
        em.merge(parent);
        em.remove(parkingSpot);
        em.close();
        return parkingSpot;
    }


    // Auto

    /**
     * Vytvorenie nového auta
     *
     * @param userId                   id používateľa/zákazníka
     * @param brand                    značka auta
     * @param model                    model auta
     * @param colour                   farba karosérie auta
     * @param vehicleRegistrationPlate evidenčné číslo vozidla
     * @return objekt entity auta
     */
    @Override
    public Object createCar(Long userId, String brand, String model, String colour, String vehicleRegistrationPlate) {
        if(userId == null || brand == null || model == null ||
                colour == null || vehicleRegistrationPlate == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<Car> query1 = em
                .createQuery("SELECT c FROM Car c WHERE c.vehicleRegistrationPlate = ?1", Car.class)
                .setParameter(1, vehicleRegistrationPlate);
        TypedQuery<User> query2 = em
                .createQuery("SELECT u FROM User u WHERE u.userId = ?1", User.class)
                .setParameter(1, userId);
        User searchedUser;
        try {
            searchedUser = query2.getSingleResult();
        }
        catch(Exception e) {
            em.close();
            return null;
        }

        try {
            query1.getSingleResult();
        }
        catch(Exception e) {
            Car car = new Car(brand, model, colour, vehicleRegistrationPlate);
            car.setUser(searchedUser);
            em.getTransaction().begin();
            em.persist(car);
            em.getTransaction().commit();
            em.close();
            return car;
        }

        em.close();
        return null;
    }

    /**
     * Získanie entity auta podľa id
     *
     * @param carId id auta
     * @return objekt entity auta
     */
    @Override
    public Object getCar(Long carId) {
        if(carId == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        Object carObject = em.find(Car.class, carId);
        em.close();
        return carObject;
    }

    /**
     * Získanie entity auta podľa EČV
     *
     * @param vehicleRegistrationPlate evidenčné číslo vozidla
     * @return objekt entity auta
     */
    @Override
    public Object getCar(String vehicleRegistrationPlate) {
        if(vehicleRegistrationPlate == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c WHERE c.vehicleRegistrationPlate = ?1", Car.class);
        Car car;
        try {
            car = query.setParameter(1, vehicleRegistrationPlate).getSingleResult();
        }
        catch(Exception e) {
            em.close();
            return null;
        }
        em.close();
        return car;
    }

    /**
     * Získanie zoznamu áut používateľa/zákazníka
     *
     * @param userId id používateľa/zákazníka
     * @return zoznam entít áut
     */
    @Override
    public List<Object> getCars(Long userId) {
        if(userId == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c WHERE c.user.userId = ?1", Car.class).setParameter(1, userId);
        List<Object> resultList = new ArrayList<>(query.getResultList());
        em.close();
        return resultList;
    }

    @Override
    public List<Object> getCars() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Car> query = em.createQuery("SELECT c FROM Car c", Car.class);
        List<Object> resultList = new ArrayList<>(query.getResultList());
        em.close();
        return resultList;
    }

    @Override
    public Object updateCar(Object car) {
        if(car == null) {
            return null;
        }
        if(!(car instanceof Car)) {
            return null;
        }
        Car newCar = (Car) car;
        if(newCar.getCarId() == null ||
                newCar.getBrand() == null ||
                newCar.getColour() == null ||
                newCar.getVehicleRegistrationPlate() == null ||
                newCar.getModel() == null) {
            return null;
        }
        String newRegistrationPlate = newCar.getVehicleRegistrationPlate();
        EntityManager em = emf.createEntityManager();
        TypedQuery<Car> query = em
                .createQuery("SELECT c FROM Car c WHERE c.vehicleRegistrationPlate = ?1", Car.class)
                .setParameter(1, newRegistrationPlate);
        boolean stop = true;
        try {
            query.getSingleResult();
        }
        catch(Exception e) {
            stop = false;
        }
        if(stop) {
            em.close();
            return null;
        }
        query = em
                .createQuery("SELECT c FROM Car c WHERE c.carId = ?1", Car.class)
                .setParameter(1, newCar.getCarId());
        Car updatedCar = null;
        try {
            updatedCar = query.getSingleResult();
        }
        catch(Exception e) {
            stop = true;
        }
        if(stop) {
            em.close();
            return null;
        }
        if(updatedCar == null) {
            em.close();
            return null;
        }
        updatedCar.copyCar(newCar);
        em.getTransaction().begin();
        em.merge(updatedCar);
        em.getTransaction().commit();
        em.close();
        em.close();
        return null;
    }

    /**
     * Vymazanie auta
     *
     * @param carId id auta
     * @return vymazaná entita auta
     */
    @Override
    public Object deleteCar(Long carId) {
        if(carId == null) {
            return null;
        }
        Car car = (Car) getCar(carId);
        if(car == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();

        List<Reservation> reservations =
                em.createQuery("SELECT r FROM Reservation r", Reservation.class)
                        .getResultList();
        for(Reservation reservation : reservations) {
            if(reservation.getCar().getCarId().equals(carId)) {
                reservation.setCar(null);
                em.merge(reservation);
            }
        }
        TypedQuery<ParkingSpot> query = em
                .createQuery("SELECT ps FROM ParkingSpot ps", ParkingSpot.class);
        List<ParkingSpot> parentList;
        try {
            parentList = query.getResultList();
        }
        catch(Exception e) {
            em.close();
            return null;
        }

        Optional<ParkingSpot> parentOptional = parentList.stream()
                .filter(parentFilter -> parentFilter.getCar().getCarId().equals(carId))
                .findAny();
        if(parentOptional.isPresent()) {
            ParkingSpot parent = parentOptional.get();
            parent.setCar(null);
            em.merge(parent);
        }
        em.getTransaction().begin();

        em.createQuery("DELETE FROM Car c WHERE c.carId = ?1")
                .setParameter(1, carId).executeUpdate();

        em.getTransaction().commit();
        em.close();
        return car;
    }


    // Používateľ / Zákazník

    /**
     * Vytvorenie používateľa / zákazníka
     *
     * @param firstname krstné meno
     * @param lastname  priezvisko
     * @param email     emailová adresa. Musí byť unikátna
     * @return objekt entity používateľa
     */
    @Override
    public Object createUser(String firstname, String lastname, String email) {
        if(firstname == null || lastname == null || email == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<User> query = em
                .createQuery("SELECT u FROM User u WHERE u.email = ?1", User.class)
                .setParameter(1, email);
        try {
            query.getSingleResult();
        }
        catch(Exception e) {
            User user = new User(firstname, lastname, email);
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            em.close();
            return user;
        }
        em.close();
        return null;
    }

    /**
     * Získanie používateľa podľa id
     *
     * @param userId id používateľa
     * @return objekt entity používateľa
     */
    @Override
    public Object getUser(Long userId) {
        if(userId == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        Object userObject = em.find(User.class, userId);
        em.close();
        return userObject;
    }

    /**
     * Získanie používateľa podľa emailovej adresy
     *
     * @param email emailová adresa používateľa
     * @return objekt entity používateľa
     */
    @Override
    public Object getUser(String email) {
        if(email == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.email = ?1", User.class);
        User user = query.setParameter(1, email).getSingleResult();
        em.close();
        return user;
    }

    /**
     * Získanie zoznamu všetkých používateľov
     *
     * @return zoznam entít používateľov
     */
    @Override
    public List<Object> getUsers() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        List<Object> resultList = new ArrayList<>(query.getResultList());
        em.close();
        return resultList;
    }

    @Override
    public Object updateUser(Object user) {
        if(user == null) {
            return null;
        }
        if(!(user instanceof User)) {
            return null;
        }
        User newUser = (User) user;
        if(newUser.getFirstname() == null ||
                newUser.getLastname() == null ||
                newUser.getEmail() == null)
        {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<User> query = em
                .createQuery("SELECT u FROM User u WHERE u.userId = ?1", User.class)
                .setParameter(1, newUser.getUserId());
        User updatedUser;
        try {
            updatedUser = query.getSingleResult();
        }
        catch(Exception e) {
            em.close();
            return null;
        }

        if(!updatedUser.getEmail().equals(newUser.getEmail())) {
            query = em
                    .createQuery("SELECT u FROM User u WHERE u.email = ?1", User.class)
                    .setParameter(1, newUser.getEmail());
            User searched;
            try {
                searched = query.getSingleResult();
            }
            catch(Exception e) {
                searched = null;
            }
            if(searched != null) {
                em.close();
                return null;
            }
        }
        updatedUser.copyUser(newUser);
        em.getTransaction().begin();
        em.merge(updatedUser);
        em.getTransaction().commit();
        em.close();
        return null;
    }

    /**
     * Vymazanie používateľa
     *
     * @param userId id používateľa
     * @return vymazaná entita používateľa
     */
    @Override
    public Object deleteUser(Long userId) {
        if(userId == null) {
            return null;
        }
        Object userObject = getUser(userId);
        if(userObject == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Object> cars = getCars(userId);
        for(Object carObject : cars) {
            Car car = (Car) carObject;
            if(car.getUser() != null) {
                if(car.getUser().getUserId().equals(userId)) {
                    deleteCar(car.getCarId());
                }
            }
        }
        TypedQuery<User> query = em
                .createQuery("DELETE FROM User u WHERE u.userId = ?1", User.class)
                .setParameter(1, userId);
        query.executeUpdate();
        em.getTransaction().commit();
        em.close();
        return userObject;
    }


    // Rezervácia / Parkovanie

    /**
     * Vytvorenie rezervácie pre zaparkované auto. Pri vytvorení rezervácie je do nej zapísaný dátum a čas začatia rezervácie.
     *
     * @param parkingSpotId id parkovacieho miesta
     * @param cardId        id auta
     * @return objekt rezervácie
     */
    @Override
    public Object createReservation(Long parkingSpotId, Long cardId) {
        if(parkingSpotId == null || cardId == null) {
            return null;
        }
        ParkingSpot parkingSpot = (ParkingSpot) getParkingSpot(parkingSpotId);
        if(parkingSpot == null) {
            return null;
        }
        if(parkingSpot.getCar() != null) {
            return null;
        }
        Car car = (Car) getCar(cardId);
        if(car == null) {
            return null;
        }
        parkingSpot.setCar(car);
        Reservation reservation = new Reservation(car, parkingSpot);
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(reservation);
        em.merge(parkingSpot);
        transaction.commit();
        em.close();
        return reservation;
    }

    /**
     * Ukončenie rezervácie / parkovanie auta. Pri ukončení parkovania je zapísaný čas ukončenia rezervácie a vypočítaná celková cena za parkovanie.
     *
     * @param reservationId id rezervácie
     * @return objekt entity rezervácie
     */
    @Override
    public Object endReservation(Long reservationId) {
        if(reservationId == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        Reservation reservation = em.find(Reservation.class, reservationId);
        if(reservation == null) {
            em.close();
            return null;
        }
        List<Object> carParksObjects = getCarParks();
        if(carParksObjects == null) {
            em.close();
            return null;
        }
        Long parkingSpotId = reservation.getParkingSpot().getParkingSpotId();
        Integer pricePerHour = null;
        for(Object carParksObject : carParksObjects) {
            boolean breakLoop = false;
            CarPark carPark = (CarPark) carParksObject;
            List<CarParkFloor> carParkFloors = carPark.getCarParkFloors();
            for(CarParkFloor carParkFloor : carParkFloors) {
                List<ParkingSpot> parkingSpots = carParkFloor.getParkingSpots();
                Optional<ParkingSpot> parkingSpotOptional = parkingSpots.stream()
                        .filter(parkingSpot -> parkingSpot.getParkingSpotId()
                                .longValue() == parkingSpotId.longValue()).findFirst();
                if(parkingSpotOptional.isPresent()) {
                    pricePerHour = ((CarPark) carParksObject).getPricePerHour();
                    breakLoop = true;
                    break;
                }
            }
            if(breakLoop) {
                break;
            }
        }
        if(pricePerHour == null) {
            em.close();
            return null;
        }
        ParkingSpot parkingSpot = reservation.getParkingSpot();
        parkingSpot.setCar(null);
        reservation.endReservation(pricePerHour, getHolidays());
        em.getTransaction().begin();
        em.merge(reservation);
        em.merge(parkingSpot);
        em.getTransaction().commit();
        em.close();
        return reservation;
    }

    @Override
    public List<Object> getAllReservations() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r", Reservation.class);
        List<Object> resultList = new ArrayList<>(query.getResultList());
        em.close();
        return resultList;
    }

    @Override
    public Object getReservation(Long id) {
        if(id == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        Object reservationObject = em.find(Reservation.class, id);
        em.close();
        return reservationObject;
    }

    /**
     * Získanie zoznamu všetkých rezervácií pre parkovacieho miesto začaté v daný deň.
     *
     * @param parkingSpotId id parkovacieho miesta
     * @param date          dátum rezervácii
     * @return zoznam entít rezervácií
     */
    @Override
    public List<Object> getReservations(Long parkingSpotId, Date date) {
        if(parkingSpotId == null || date == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<Reservation> query = em.createQuery("SELECT r FROM Reservation r", Reservation.class);
        List<Object> resultList = new ArrayList<>(query.getResultList());
        em.close();
        List<Object> reservationsObjects = new ArrayList<>();
        for(Object reservationObject : resultList) {
            Reservation reservation = (Reservation) reservationObject;
            if(reservation.getParkingSpot().getParkingSpotId()
                    .longValue() == parkingSpotId.longValue()) {
                if(isSameDay(date, reservation.getStartDate())) {
                    reservationsObjects.add(reservationObject);
                }
            }
        }
        return reservationsObjects;
    }

    /**
     * Získanie zoznamu aktívnych / neukončených rezervácií pre daného používateľa.
     *
     * @param userId id používateľa
     * @return zoznam entít rezervácií
     */
    @Override
    public List<Object> getMyReservations(Long userId) {
        if(userId == null) {
            return null;
        }
        List<Object> carsObjects = getCars(userId);
        if(carsObjects == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        TypedQuery<Reservation> query =
                em.createQuery("SELECT r FROM Reservation r WHERE r.endDate = null AND r.car.user.userId = ?1", Reservation.class)
                        .setParameter(1, userId);
        List<Object> resultList = new ArrayList<>(query.getResultList());
        em.close();
        return resultList;
    }

    @Override
    public Object updateReservation(Object reservation) {
//        if(reservation == null) {
//            return null;
//        }
//        if(!(reservation instanceof Reservation)) {
//            return null;
//        }
//
//        EntityManager em = emf.createEntityManager();
//        if(!em.contains(reservation)) {
//            em.close();
//            return null;
//        }
//        em.getTransaction().begin();
//        em.merge(reservation);
//        em.getTransaction().commit();
//        em.close();
        return null;
    }


    // Skupina C

    /**
     * Vytvorenie sviatku
     *
     * @param name názov sviatku
     * @param date dátum sviatku
     * @return objekt entity sviatku
     */
    public Object createHoliday(String name, Date date) {
        if(name == null || date == null) {
            return null;
        }
        List<Object> holidaysObjects = getHolidays();
        for(Object holidaysObject : holidaysObjects) {
            Holiday checkedHoliday = (Holiday) holidaysObject;
            if(isSameDay(checkedHoliday.getDate(), date)){
                return null;
            }
        }

        EntityManager em = emf.createEntityManager();
        Holiday holiday = new Holiday(name, date);
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        em.persist(holiday);
        transaction.commit();
        em.close();
        return holiday;
    }

    public List<Object> getHolidays(Date date) {
        if(date == null) {
            return null;
        }
        List<Object> holidayObjects = getHolidays();
        if(holidayObjects == null) {
            return null;
        }
        List<Object> result = new ArrayList<>();
        for(Object holidayObject : holidayObjects) {
            Holiday holiday = (Holiday) holidayObject;
            if(isSameDay(holiday.getDate(), date)) {
                result.add(holidayObject);
            }
        }
        return result;
    }
    /**
     * Získanie sviatku
     *
     * @param date dátum sviatku
     * @return objekt entity sviatku
     */
    public Object getHoliday(Date date) {
        if(date == null) {
            return null;
        }
        List<Object> holidaysObjects = getHolidays();
        if(holidaysObjects == null) {
            return null;
        }
        for(Object holidaysObject : holidaysObjects) {
            Holiday holiday = (Holiday) holidaysObject;
            if(isSameDay(holiday.getDate(), date)) {
                return holidaysObject;
            }
        }
        return null;
    }

    /**
     * Získanie zoznamu všetkých sviatkov
     *
     * @return zoznam entít sviatkov
     */
    @Override
    public List<Object> getHolidays() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Holiday> query = em.createQuery("SELECT h FROM Holiday h", Holiday.class);
        List<Object> resultList = new ArrayList<>(query.getResultList());
        em.close();
        return resultList;
    }

    /**
     * Vymazanie sviatku
     *
     * @param holidayId id sviatku
     * @return vymazaná entita sviatku
     */
    public Object deleteHoliday(Long holidayId) {
        if(holidayId == null) {
            return null;
        }
        EntityManager em = emf.createEntityManager();
        Holiday holiday = em.find(Holiday.class, holidayId);
        if(holiday == null) {
            return null;
        }
        em.getTransaction().begin();
        TypedQuery<CarPark> query = em
                .createQuery("DELETE FROM Holiday h WHERE h.holidayId = ?1", CarPark.class)
                .setParameter(1, holidayId);
        query.executeUpdate();
        em.getTransaction().commit();
        em.close();
        return holiday;
    }


    public Boolean authorize(String authorizationHeader) {
        String base64Encoded = authorizationHeader.substring("Basic ".length());
        String decoded = new String(Base64.getDecoder().decode(base64Encoded));
        String[] credentials = decoded.split(":");
        String userMail = credentials[0];
        String userId = credentials[1];

        Object userObject = getUser(userMail);
        if(userObject == null) {
            return false;
        }
        User user = (User) userObject;
        return user.getUserId().toString().equals(userId);
    }

    public Object merge(Object toMerge) {
        EntityManager em = emf.createEntityManager();
        Object merged = em.merge(toMerge);
        em.close();
        return merged;
    }
}
