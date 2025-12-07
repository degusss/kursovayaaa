import repository.CSVAircraftRepository;
import service.AircraftService;
import ui.ConsoleUI;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Точка входа в приложение управления авиационным парком.
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Инициализация системы логирования;</li>
 *     <li>Создание репозитория и сервисного слоя;</li>
 *     <li>Запуск консольного пользовательского интерфейса {@link ui.ConsoleUI};</li>
 *     <li>Загрузка данных из CSV при помощи {@link CSVAircraftRepository}.</li>
 * </ul>
 *
 * <p>Данный класс запускает приложение в консольном режиме,
 * что удобно для тестирования, отладки и демонстрации логики.</p>
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    /**
     * Точка входа в приложение.
     *
     * @param args параметры командной строки
     */
    public static void main(String[] args) {

        logger.info("Приложение запущено");
        logger.debug("Отладка включена");
        logger.error("Проверка ERROR лога");

        System.out.println("Система авиационного парка запущена!");

        String csvPath = "data/aircrafts.csv";

        AircraftService service = new AircraftService(
                new CSVAircraftRepository(csvPath)
        );

        logger.info("Создан сервис и UI");

        new ConsoleUI(service).start();
    }
}
