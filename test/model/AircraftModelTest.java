package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftModelTest {

    @Test
    void passengerAircraft_basicPropertiesAndToCsv() {
        PassengerAircraft p = new PassengerAircraft(
                "P1",
                "A320",
                "Airbus",
                180,
                6100.0,
                2015,
                4500,
                "Эксплуатируется",
                "Эконом"
        );

        assertEquals("P1", p.getId());
        assertEquals("A320", p.getModel());
        assertEquals("Airbus", p.getManufacturer());
        assertEquals(180, p.getCapacity());
        assertEquals(6100.0, p.getRange());
        assertEquals(2015, p.getYear());
        assertEquals(4500, p.getFlightHours());
        assertEquals("Эксплуатируется", p.getStatus());
        assertEquals("Эконом", p.getCabinClass());

        assertEquals("Passenger aircraft", p.getAircraftType());

        // Проверяем CSV-строку: содержит тип в начале и cabinClass в конце (разделитель ;)
        String csv = p.toCSV();
        assertTrue(csv.startsWith("Passenger aircraft;"));
        String[] parts = csv.split(";");
        // expected columns: type,id,model,manufacturer,capacity,range,year,flightHours,status,cabinClass
        assertEquals(10, parts.length);
        assertEquals("P1", parts[1]);
        assertEquals("A320", parts[2]);
        assertEquals("Эконом", parts[9]);
    }

    @Test
    void cargoAircraft_basicPropertiesAndToCsv() {
        CargoAircraft c = new CargoAircraft(
                "C1",
                "IL-76",
                "Ilyushin",
                0,
                4500.0,
                2010,
                12000,
                "В ремонте",
                50000.0
        );

        assertEquals("C1", c.getId());
        assertEquals("IL-76", c.getModel());
        assertEquals("Ilyushin", c.getManufacturer());
        assertEquals(0, c.getCapacity());
        assertEquals(4500.0, c.getRange());
        assertEquals(2010, c.getYear());
        assertEquals(12000, c.getFlightHours());
        assertEquals("В ремонте", c.getStatus());
        assertEquals(50000.0, c.getMaxCargoWeight());

        assertEquals("Cargo aircraft", c.getAircraftType());

        String csv = c.toCSV();
        assertTrue(csv.startsWith("Cargo aircraft;"));
        String[] parts = csv.split(";");
        assertEquals(10, parts.length);
        assertEquals("C1", parts[1]);
        // last column is maxCargoWeight
        assertEquals("50000.0", parts[9]);
    }

    @Test
    void militaryAircraft_basicPropertiesAndToCsv() {
        MilitaryAircraft m = new MilitaryAircraft(
                "M1",
                "Su-35",
                "Sukhoi",
                1,
                3600.0,
                2018,
                800,
                "На базе",
                "Ракеты"
        );

        assertEquals("M1", m.getId());
        assertEquals("Su-35", m.getModel());
        assertEquals("Sukhoi", m.getManufacturer());
        assertEquals(1, m.getCapacity());
        assertEquals(3600.0, m.getRange());
        assertEquals(2018, m.getYear());
        assertEquals(800, m.getFlightHours());
        assertEquals("На базе", m.getStatus());
        assertEquals("Ракеты", m.getWeaponType());

        assertEquals("Military aircraft", m.getAircraftType());

        String csv = m.toCSV();
        assertTrue(csv.startsWith("Military aircraft;"));
        String[] parts = csv.split(";");
        assertEquals(10, parts.length);
        assertEquals("M1", parts[1]);
        assertEquals("Ракеты", parts[9]);
    }
}
