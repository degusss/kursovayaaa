package repository;

import model.Aircraft;
import java.util.List;

/**
 * Базовый интерфейс репозитория самолётов.
 * Определяет минимальный набор операций для работы с коллекцией
 * объектов {@link Aircraft}, независимо от способа хранения данных.
 *
 * <p>Реализован способами:
 * <ul>
 *     <li>Хранение в памяти ({@link InMemoryAircraftRepository})</li>
 *     <li>Хранение в CSV-файле ({@link CSVAircraftRepository})</li>
 * </ul>
 * </p>
 */
public interface AircraftRepository {

    /**
     * Добавляет новый самолёт в хранилище.
     *
     * @param aircraft объект самолёта
     */
    void add(Aircraft aircraft);

    /**
     * Возвращает список всех самолётов.
     *
     * @return неизменяемый список объектов {@link Aircraft}
     */
    List<Aircraft> getAll();

    /**
     * Выполняет поиск самолёта по идентификатору.
     *
     * @param id идентификатор
     * @return объект {@link Aircraft} или {@code null}, если не найден
     */
    Aircraft findById(String id);

    /**
     * Удаляет самолёт по идентификатору.
     *
     * @param id идентификатор
     * @return {@code true}, если удаление произошло успешно
     */
    boolean remove(String id);
}
