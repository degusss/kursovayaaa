package repository;

import model.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVAircraftRepositoryTest {

    private static final String TEST_FILE = "test_aircraft.csv";

    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    @Test
    void testLoadFromFile() throws IOException {
        // Создаём CSV вручную
        Files.writeString(Paths.get(TEST_FILE),
                "Passenger aircraft;1;a1;airbus;150;12000.0;2020;1500;Ремонт;Эконом\n" +
                        "Cargo aircraft;2;a2;boeing;0;8000.0;2018;3000;Рабочий;5000\n" +
                        "Military aircraft;3;a3;sukhoi;1;1500.0;2010;700;Боевой;Ракеты\n"
        );

        CSVAircraftRepository repo = new CSVAircraftRepository(TEST_FILE);
        List<Aircraft> list = repo.getAll();

        assertEquals(3, list.size());

        // Проверяем пассажирский
        PassengerAircraft p = (PassengerAircraft) list.get(0);
        assertEquals("1", p.getId());
        assertEquals("a1", p.getModel());
        assertEquals("airbus", p.getManufacturer());
        assertEquals(150, p.getCapacity());
        assertEquals(12000.0, p.getRange());
        assertEquals(2020, p.getYear());
        assertEquals(1500, p.getFlightHours());
        assertEquals("Ремонт", p.getStatus());
        assertEquals("Эконом", p.getCabinClass());
    }

    @Test
    void testAddAndSave() {
        CSVAircraftRepository repo = new CSVAircraftRepository(TEST_FILE);

        PassengerAircraft p = new PassengerAircraft(
                "10", "m1", "airbus", 100, 5000.0,
                2019, 1000, "OK", "Бизнес"
        );

        repo.add(p);

        // Перечитываем CSV из файла
        CSVAircraftRepository repo2 = new CSVAircraftRepository(TEST_FILE);
        Aircraft loaded = repo2.findById("10");

        assertNotNull(loaded);
        assertEquals("m1", loaded.getModel());
        assertEquals("Бизнес", ((PassengerAircraft) loaded).getCabinClass());
    }

    @Test
    void testRemove() {
        CSVAircraftRepository repo = new CSVAircraftRepository(TEST_FILE);

        repo.add(new PassengerAircraft(
                "5", "m5", "tu", 120, 6000.0,
                2015, 2000, "OK", "Эконом"
        ));

        assertTrue(repo.remove("5"));

        CSVAircraftRepository repo2 = new CSVAircraftRepository(TEST_FILE);
        assertNull(repo2.findById("5"));
    }
}
