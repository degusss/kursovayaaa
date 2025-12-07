package service;

import model.PassengerAircraft;
import model.Aircraft;
import repository.InMemoryAircraftRepository;
import exceptions.AircraftNotFoundException;
import exceptions.DuplicateIdException;
import exceptions.InvalidAircraftDataException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AircraftServiceTest {

    private AircraftService service;

    @BeforeEach
    void setup() {
        service = new AircraftService(new InMemoryAircraftRepository());
    }

    private PassengerAircraft sample() {
        return new PassengerAircraft(
                "A1", "Boeing 737", "Boeing",
                150, 5000, 2010, 2000,
                "Рабочий", "Эконом"
        );
    }

    // -----------------------------------------------------------
    // ADD
    // -----------------------------------------------------------
    @Test
    void testAddAircraft_success() {
        PassengerAircraft a = sample();

        service.addAircraft(a);

        Aircraft found = service.findAircraft("A1");

        assertEquals("Boeing 737", found.getModel());
        assertEquals(150, found.getCapacity());
    }

    @Test
    void testAddAircraft_duplicateId() {
        PassengerAircraft a = sample();
        PassengerAircraft b = sample(); // тот же ID

        service.addAircraft(a);

        assertThrows(DuplicateIdException.class, () -> service.addAircraft(b));
    }

    @Test
    void testAddAircraft_emptyId() {
        PassengerAircraft a = sample();
        a.setID("");

        assertThrows(InvalidAircraftDataException.class, () -> service.addAircraft(a));
    }

    @Test
    void testAddAircraft_negativeCapacity() {
        PassengerAircraft a = sample();
        a.setCapacity(-10);

        assertThrows(InvalidAircraftDataException.class, () -> service.addAircraft(a));
    }

    @Test
    void testAddAircraft_negativeRange() {
        PassengerAircraft a = sample();
        a.setRange(-100);

        assertThrows(InvalidAircraftDataException.class, () -> service.addAircraft(a));
    }

    // -----------------------------------------------------------
    // FIND
    // -----------------------------------------------------------
    @Test
    void testFindAircraft_success() {
        PassengerAircraft a = sample();
        service.addAircraft(a);

        Aircraft found = service.findAircraft("A1");

        assertNotNull(found);
        assertEquals("A1", found.getId());
    }

    @Test
    void testFindAircraft_notFound() {
        assertThrows(AircraftNotFoundException.class, () -> service.findAircraft("XXX"));
    }

    // -----------------------------------------------------------
    // REMOVE
    // -----------------------------------------------------------
    @Test
    void testRemoveAircraft_success() {
        PassengerAircraft a = sample();
        service.addAircraft(a);

        boolean removed = service.removeAircraft("A1");

        assertTrue(removed);
        assertThrows(AircraftNotFoundException.class, () -> service.findAircraft("A1"));
    }

    @Test
    void testRemoveAircraft_notFound() {
        assertThrows(AircraftNotFoundException.class, () -> service.removeAircraft("QWERTY"));
    }

    // -----------------------------------------------------------
    // GET ALL
    // -----------------------------------------------------------
    @Test
    void testGetAllAircraft() {
        service.addAircraft(sample());
        service.addAircraft(new PassengerAircraft(
                "A2", "Airbus A320", "Airbus",
                160, 5500, 2012, 1500,
                "Рабочий", "Эконом"
        ));

        List<Aircraft> list = service.getAllAircraft();

        assertEquals(2, list.size());
    }

    // -----------------------------------------------------------
    // ANALYTICS
    // -----------------------------------------------------------
    @Test
    void testAverageCapacity() {
        service.addAircraft(sample()); // capacity = 150
        service.addAircraft(new PassengerAircraft(
                "A2", "A320", "Airbus",
                250, 6000, 2015, 1800,
                "OK", "Эконом"
        ));

        assertEquals(200.0, service.averageCapacity());
    }

    @Test
    void testMaxRangeAircraft() {
        service.addAircraft(sample()); // 5000
        service.addAircraft(new PassengerAircraft(
                "A2", "A310", "Airbus",
                130, 8000, 2005, 5000,
                "OK", "Эконом"
        ));

        Aircraft max = service.maxRangeAircraft();

        assertEquals("A2", max.getId());
    }

    @Test
    void testOldestAircraft() {
        service.addAircraft(sample()); // 2010
        service.addAircraft(new PassengerAircraft(
                "A2", "TU-154", "Tupolev",
                150, 3000, 1990, 9000,
                "Старый", "Эконом"
        ));

        Aircraft old = service.oldestAircraft();

        assertEquals("A2", old.getId());
    }
}
