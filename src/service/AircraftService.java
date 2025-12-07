package service;

import model.Aircraft;
import repository.AircraftRepository;
import exceptions.AircraftNotFoundException;
import exceptions.DuplicateIdException;
import exceptions.InvalidAircraftDataException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Service класс, обеспечивающий логику работы с сущностями {@link Aircraft}.
 * <p>
 * Выполняет:
 * <ul>
 *     <li>Проверку корректности данных перед добавлением;</li>
 *     <li>Поиск самолётов по ID;</li>
 *     <li>Удаление самолётов;</li>
 *     <li>Аналитические вычисления (средняя вместимость, максимальная дальность и т.д.);</li>
 *     <li>Логирование всех операций.</li>
 * </ul>
 * <p>
 * Работает поверх абстракции {@link AircraftRepository}, что позволяет легко менять
 * тип хранения данных (CSV, in-memory, база данных и т.д.).
 */
public class AircraftService {

    private static final Logger logger = LogManager.getLogger(AircraftService.class);

    private final AircraftRepository repository;

    /**
     * Создаёт экземпляр сервисного класса.
     *
     * @param repository репозиторий, в котором хранятся самолёты
     */
    public AircraftService(AircraftRepository repository) {
        this.repository = repository;
        logger.info("AircraftService инициализирован");
    }

    // ---------------- GET ALL ----------------

    /**
     * Возвращает список всех самолётов, доступных в репозитории.
     *
     * @return список всех самолётов
     */
    public List<Aircraft> getAllAircraft() {
        logger.info("Получение списка всех самолётов ({} шт.)", repository.getAll().size());
        return repository.getAll();
    }

    // ---------------- ADD ----------------

    /**
     * Добавляет новый самолёт в систему.
     * Выполняет проверку данных и проверку на дубликат ID.
     *
     * @param aircraft объект самолёта
     * @throws InvalidAircraftDataException если обнаружены некорректные данные
     * @throws DuplicateIdException         если самолёт с таким ID уже существует
     */
    public void addAircraft(Aircraft aircraft) {

        logger.info("Попытка добавить самолёт ID={}", aircraft.getId());

        if (aircraft.getId() == null || aircraft.getId().isBlank()) {
            logger.error("Ошибка: ID пустой");
            throw new InvalidAircraftDataException("ID не может быть пустым");
        }

        if (aircraft.getCapacity() < 0) {
            logger.error("Ошибка: отрицательная вместимость {}", aircraft.getCapacity());
            throw new InvalidAircraftDataException("Вместимость не может быть отрицательной");
        }

        if (aircraft.getRange() < 0) {
            logger.error("Ошибка: отрицательная дальность {}", aircraft.getRange());
            throw new InvalidAircraftDataException("Дальность не может быть отрицательной");
        }

        if (repository.findById(aircraft.getId()) != null) {
            logger.error("Дубликат ID: {}", aircraft.getId());
            throw new DuplicateIdException("Самолёт с ID " + aircraft.getId() + " уже существует");
        }

        repository.add(aircraft);
        logger.info("Самолёт ID={} успешно добавлен ({} {})",
                aircraft.getId(),
                aircraft.getAircraftType(),
                aircraft.getModel()
        );
    }

    // ---------------- FIND ----------------

    /**
     * Ищет самолёт по его ID.
     *
     * @param id идентификатор самолёта
     * @return найденный объект {@link Aircraft}
     * @throws AircraftNotFoundException если указанный ID отсутствует в системе
     */
    public Aircraft findAircraft(String id) {

        logger.info("Поиск самолёта ID={}", id);

        Aircraft a = repository.findById(id);

        if (a == null) {
            logger.warn("Самолёт ID={} не найден", id);
            throw new AircraftNotFoundException("Самолёт с ID " + id + " не найден");
        }

        logger.info("Самолёт ID={} найден ({} {})", id, a.getAircraftType(), a.getModel());
        return a;
    }

    // ---------------- REMOVE ----------------

    /**
     * Удаляет самолёт с указанным ID.
     *
     * @param id идентификатор удаляемого самолёта
     * @return {@code true}, если самолёт был удалён
     * @throws AircraftNotFoundException если самолёт с указанным ID отсутствует
     */
    public boolean removeAircraft(String id) {

        logger.info("Попытка удалить самолёт ID={}", id);

        Aircraft target = repository.findById(id);

        if (target == null) {
            logger.warn("Удаление невозможно — ID={} не найден", id);
            throw new AircraftNotFoundException("Нельзя удалить — самолёт не найден");
        }

        boolean result = repository.remove(id);

        if (result)
            logger.info("Самолёт ID={} удалён ({} {})",
                    id,
                    target.getAircraftType(),
                    target.getModel()
            );
        else
            logger.error("Ошибка удаления самолёта ID={}", id);

        return result;
    }

    // ---------------- ANALYTICS ----------------

    /**
     * Вычисляет среднюю пассажировместимость всех самолётов.
     *
     * @return средняя вместимость или 0, если список пуст
     */
    public double averageCapacity() {
        logger.info("Расчёт средней вместимости");
        return repository.getAll().stream()
                .mapToInt(Aircraft::getCapacity)
                .average()
                .orElse(0);
    }

    /**
     * Находит самолёт с максимальной дальностью полёта.
     *
     * @return самолёт с наибольшей дальностью или {@code null}, если список пуст
     */
    public Aircraft maxRangeAircraft() {
        logger.info("Поиск самолёта с максимальной дальностью");
        Aircraft result = repository.getAll().stream()
                .max((a, b) -> Double.compare(a.getRange(), b.getRange()))
                .orElse(null);

        if (result != null)
            logger.info("Максимальная дальность: {} у {}", result.getRange(), result.getId());

        return result;
    }

    /**
     * Находит самый старый самолёт в системе (по году выпуска).
     *
     * @return самолёт с минимальным годом выпуска или {@code null}, если список пуст
     */
    public Aircraft oldestAircraft() {
        logger.info("Поиск самого старого самолёта");
        Aircraft result = repository.getAll().stream()
                .min((a, b) -> Integer.compare(a.getYear(), b.getYear()))
                .orElse(null);

        if (result != null)
            logger.info("Самый старый самолёт: {} года, ID={}", result.getYear(), result.getId());

        return result;
    }
}
