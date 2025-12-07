package repository;

import model.PassengerAircraft;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryAircraftRepositoryTest {

    private InMemoryAircraftRepository repository;

    @BeforeEach
    void setup() {
        repository = new InMemoryAircraftRepository();
    }

    // --------------------------------------------------------
    // ADD
    // --------------------------------------------------------
    @Test
    void testAddAircraft() {
        PassengerAircraft a = new PassengerAircraft(
                "1", "A320", "Airbus",
                180, 6100, 2010, 5000,
                "OK", "Economy"
        );

        repository.add(a);

        assertEquals(1, repository.getAll().size());
        assertEquals(a, repository.findById("1"));
    }

    // --------------------------------------------------------
    // ADD DUPLICATE (ожидаем исключение)
    // --------------------------------------------------------
    @Test
    void testAddDuplicateIdThrowsException() {
        PassengerAircraft a1 = new PassengerAircraft(
                "1", "A320", "Airbus",
                180, 6000, 2010, 4000,
                "OK", "Business"
        );
        PassengerAircraft a2 = new PassengerAircraft(
                "1", "B737", "Boeing",
                150, 5500, 2012, 3500,
                "OK", "Economy"
        );

        repository.add(a1);

        assertThrows(IllegalArgumentException.class, () -> repository.add(a2));
    }

    // --------------------------------------------------------
    // FIND BY ID
    // --------------------------------------------------------
    @Test
    void testFindByIdExists() {
        PassengerAircraft a = new PassengerAircraft(
                "2", "A330", "Airbus",
                250, 12000, 2015, 6000,
                "OK", "Economy"
        );

        repository.add(a);

        assertNotNull(repository.findById("2"));
        assertEquals("A330", repository.findById("2").getModel());
    }

    @Test
    void testFindByIdNotExists() {
        assertNull(repository.findById("999"));
    }

    // --------------------------------------------------------
    // REMOVE
    // --------------------------------------------------------
    @Test
    void testRemoveAircraft() {
        PassengerAircraft a = new PassengerAircraft(
                "3", "B777", "Boeing",
                300, 14000, 2018, 2000,
                "OK", "First"
        );

        repository.add(a);

        assertTrue(repository.remove("3"));
        assertNull(repository.findById("3"));
        assertEquals(0, repository.getAll().size());
    }

    @Test
    void testRemoveAircraftNotExists() {
        assertFalse(repository.remove("123"));
    }

    // --------------------------------------------------------
    // GET ALL
    // --------------------------------------------------------
    @Test
    void testGetAllReturnsCorrectList() {
        repository.add(new PassengerAircraft(
                "1", "A320", "Airbus",
                180, 6100, 2010, 5000,
                "OK", "Economy"
        ));

        repository.add(new PassengerAircraft(
                "2", "B737", "Boeing",
                160, 5500, 2011, 3000,
                "OK", "Business"
        ));

        assertEquals(2, repository.getAll().size());
    }
}
