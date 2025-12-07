package repository;

import model.Aircraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Простая реализация {@link AircraftRepository}, хранящая данные
 * в оперативной памяти. Не выполняет сохранение на диск и предназначена
 * для тестирования или временных операций.
 */
public class InMemoryAircraftRepository implements AircraftRepository {

    /** Внутренний список самолётов. */
    private final List<Aircraft> aircraftList = new ArrayList<>();

    /**
     * Добавляет самолёт в список.
     * Генерирует исключение, если самолёт с таким ID уже существует.
     *
     * @param aircraft объект самолёта
     * @throws IllegalArgumentException если ID уже присутствует
     */
    @Override
    public void add(Aircraft aircraft) {

        if (findById(aircraft.getId()) != null) {
            throw new IllegalArgumentException("Самолёт с таким ID уже существует: " + aircraft.getId());
        }

        aircraftList.add(aircraft);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Aircraft> getAll() {
        return aircraftList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Aircraft findById(String id) {
        return aircraftList.stream()
                .filter(a -> a.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean remove(String id) {
        return aircraftList.removeIf(a -> a.getId().equalsIgnoreCase(id));
    }
}
