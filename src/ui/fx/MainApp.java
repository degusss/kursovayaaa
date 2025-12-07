package ui.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.CSVAircraftRepository;
import service.AircraftService;

/**
 * Главный JavaFX-класс приложения.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Инициализацию {@link AircraftService};</li>
 *     <li>Загрузку основного FXML-файла;</li>
 *     <li>Создание окна и сцены;</li>
 *     <li>Передачу сервиса главному контроллеру {@link MainController}.</li>
 * </ul>
 * Запуск происходит через метод {@link #main(String[])}.
 */
public class MainApp extends Application {

    /**
     * Общий сервис, доступный контроллерам UI.
     * Инициализируется в методе {@link #main(String[])} до вызова {@link #launch}.
     */
    private static AircraftService aircraftService;

    /**
     * Точка входа JavaFX.
     * Загружает главный интерфейс из FXML, создаёт окно
     * и передаёт сервис контроллеру.
     *
     * @param stage главный контейнер JavaFX-приложения
     * @throws Exception если FXML-файл не удалось загрузить
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/main-view.fxml")
        );

        Parent root = loader.load();

        Scene scene = new Scene(root, 900, 600);

        // Передаём сервис в основной контроллер
        MainController controller = loader.getController();
        controller.setService(aircraftService);

        stage.setTitle("Aircraft Management System");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Основная точка запуска приложения.
     * <p>
     * Здесь создаётся {@link AircraftService}, затем вызывается
     * {@link Application#launch(String...)} для запуска JavaFX.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {

        String csvPath = "data/aircrafts.csv";

        aircraftService = new AircraftService(
                new CSVAircraftRepository(csvPath)
        );

        launch(args);
    }
}
