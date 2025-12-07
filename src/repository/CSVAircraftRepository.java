package repository;

import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Реализация {@link AircraftRepository}, использующая CSV-файл для хранения данных.
 * Автоматически загружает содержимое файла при создании экземпляра
 * и сохраняет изменения при добавлении или удалении записей.
 *
 * <p>Поддерживает три типа самолётов:
 * <ul>
 *     <li>{@link PassengerAircraft}</li>
 *     <li>{@link CargoAircraft}</li>
 *     <li>{@link MilitaryAircraft}</li>
 * </ul>
 * </p>
 */
public class CSVAircraftRepository implements AircraftRepository {

    private static final Logger logger = LogManager.getLogger(CSVAircraftRepository.class);

    /** Путь к CSV-файлу. */
    private final String filePath;

    /** Внутренний список самолётов, синхронизированный с CSV. */
    private final List<Aircraft> aircraftList = new ArrayList<>();

    /**
     * Создаёт объект репозитория и загружает данные из указанного CSV-файла.
     *
     * @param filePath путь к CSV-файлу
     */
    public CSVAircraftRepository(String filePath) {
        this.filePath = filePath;
        load();
    }

    /**
     * Перезагружает данные из файла (заменяет текущую коллекцию).
     */
    public void load() {
        aircraftList.clear();
        loadFromFile();
    }

    /**
     * Аналогично {@link #load()}, предоставляет удобное имя метода.
     */
    public void reload() {
        load();
    }

    /**
     * Загружает данные из CSV-файла и преобразует строки в объекты {@link Aircraft}.
     * Поддерживает автоматическое определение типа самолёта.
     */
    private void loadFromFile() {
        logger.info("Загрузка данных из CSV: {}", filePath);

        if (!Files.exists(Paths.get(filePath))) {
            logger.warn("CSV файл не найден, создаётся новый: {}", filePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = br.readLine()) != null) {

                logger.debug("CSV строка: {}", line);

                try {
                    String[] p = line.split(";");
                    String type = p[0];

                    Aircraft aircraft = switch (type) {
                        case "Passenger aircraft" -> new PassengerAircraft(
                                p[1], p[2], p[3],
                                Integer.parseInt(p[4]),
                                Double.parseDouble(p[5]),
                                Integer.parseInt(p[6]),
                                Integer.parseInt(p[7]),
                                p[8],
                                p[9]
                        );
                        case "Cargo aircraft" -> new CargoAircraft(
                                p[1], p[2], p[3],
                                Integer.parseInt(p[4]),
                                Double.parseDouble(p[5]),
                                Integer.parseInt(p[6]),
                                Integer.parseInt(p[7]),
                                p[8],
                                Double.parseDouble(p[9])
                        );
                        case "Military aircraft" -> new MilitaryAircraft(
                                p[1], p[2], p[3],
                                Integer.parseInt(p[4]),
                                Double.parseDouble(p[5]),
                                Integer.parseInt(p[6]),
                                Integer.parseInt(p[7]),
                                p[8],
                                p[9]
                        );
                        default -> {
                            logger.warn("Неизвестный тип записи в CSV: {}", type);
                            yield null;
                        }
                    };

                    if (aircraft != null) {
                        aircraftList.add(aircraft);
                        logger.info("Загружен самолёт ID={} Тип={} Модель={}",
                                aircraft.getId(),
                                aircraft.getAircraftType(),
                                aircraft.getModel());
                    }

                } catch (Exception e) {
                    logger.error("Ошибка в строке CSV: {}", line, e);
                }
            }

            logger.info("CSV загружен. Самолётов: {}", aircraftList.size());

        } catch (IOException e) {
            logger.error("Ошибка чтения CSV файла {}", filePath, e);
        }
    }

    /**
     * Сохраняет все текущие данные репозитория в CSV-файл.
     * Каждая строка формируется методом {@link Aircraft#toCSV()}.
     */
    private void saveToFile() {
        logger.info("Сохранение CSV в файл: {}", filePath);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            for (Aircraft a : aircraftList) {
                String csvLine = a.toCSV();

                logger.debug("Запись CSV строки: {}", csvLine);

                bw.write(csvLine);
                bw.newLine();

                logger.info("Сохранён самолёт: ID={} Тип={} Модель={}",
                        a.getId(),
                        a.getAircraftType(),
                        a.getModel());
            }

            logger.info("CSV сохранён. Записано {} строк.", aircraftList.size());

        } catch (IOException e) {
            logger.error("Ошибка записи CSV файла {}", filePath, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return копию внутреннего списка самолётов
     */
    @Override
    public List<Aircraft> getAll() {
        return new ArrayList<>(aircraftList);
    }

    /**
     * {@inheritDoc}
     *
     * <p>После добавления самолёта данные автоматически сохраняются в CSV-файл.</p>
     */
    @Override
    public void add(Aircraft aircraft) {
        aircraftList.add(aircraft);
        logger.info("Добавлен самолёт ID={}", aircraft.getId());
        saveToFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Aircraft findById(String id) {
        return aircraftList.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     *
     * <p>При удалении самолета, изменения сохраняются в CSV-файл.</p>
     */
    @Override
    public boolean remove(String id) {
        boolean removed = aircraftList.removeIf(a -> a.getId().equals(id));
        if (removed) {
            logger.info("Удалён самолёт ID={}", id);
            saveToFile();
        }
        return removed;
    }
}
