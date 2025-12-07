package ui.fx;

import exceptions.AircraftNotFoundException;
import exceptions.DuplicateIdException;
import exceptions.InvalidAircraftDataException;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import javafx.fxml.FXML;
import javafx.geometry.Insets;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import model.Aircraft;
import model.CargoAircraft;
import model.MilitaryAircraft;
import model.PassengerAircraft;

import service.AircraftService;

import java.util.Optional;

/**
 * Главный контроллер JavaFX-интерфейса.
 * <p>
 * Отвечает за:
 * <ul>
 *     <li>Отображение списка самолётов в таблице;</li>
 *     <li>Фильтрацию и поиск;</li>
 *     <li>Добавление, удаление и обновление данных;</li>
 *     <li>Открытие окна аналитики;</li>
 *     <li>Показ информационных/ошибочных уведомлений;</li>
 *     <li>Взаимодействие пользователя с UI.</li>
 * </ul>
 */
public class MainController {

    // ========================= UI элементы =========================

    /**
     Кнопка добавления самолёта.
     */
    @FXML private Button btnAdd;

    /**
     Кнопка удаления выбранного самолёта.
     */
    @FXML private Button btnDelete;

    /**
     Кнопка обновления таблицы.
     */
    @FXML private Button btnRefresh;

    /**
     Кнопка открытия окна аналитики.
     */
    @FXML private Button btnAnalytics;

    /**
     Основная таблица со списком самолётов.
     */
    @FXML private TableView<Aircraft> tableAircraft;

    /**
     Поле поиска по строке.
     */
    @FXML private TextField searchField;

    /**
     Фильтр по типу самолёта.
     */
    @FXML private ComboBox<String> typeFilter;

    // ========================= Данные =========================

    /**
     Сервис управления самолётами.
     */
    private AircraftService service;

    /**
     Полный список самолётов.
     */
    private ObservableList<Aircraft> masterList;

    /**
     Отфильтрованный список.
     */
    private FilteredList<Aircraft> filteredList;

    /**
     Отсортированный список.
     */
    private SortedList<Aircraft> sortedList;

    // ============================================================
    // ИНИЦИАЛИЗАЦИЯ UI
    // ============================================================

    /**
     * Инициализация контроллера. Вызывается автоматически JavaFX.
     * <p>
     * Настраивает:
     * <ul>
     *     <li>Колонки таблицы;</li>
     *     <li>Обработчики кнопок;</li>
     *     <li>Двойной клик по строке (открытие подробностей);</li>
     * </ul>
     */
    @FXML
    private void initialize() {

        if (tableAircraft.getColumns().isEmpty()) {

            TableColumn<Aircraft, String> colId = new TableColumn<>("ID");
            TableColumn<Aircraft, String> colType = new TableColumn<>("Type");
            TableColumn<Aircraft, String> colModel = new TableColumn<>("Model");
            TableColumn<Aircraft, String> colManufacturer = new TableColumn<>("Manufacturer");
            TableColumn<Aircraft, String> colCapacity = new TableColumn<>("Capacity");
            TableColumn<Aircraft, String> colRange = new TableColumn<>("Range");
            TableColumn<Aircraft, String> colYear = new TableColumn<>("Year");
            TableColumn<Aircraft, String> colHours = new TableColumn<>("FlightHours");
            TableColumn<Aircraft, String> colStatus = new TableColumn<>("Status");
            TableColumn<Aircraft, String> colSpecific = new TableColumn<>("Specific");

            colId.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getId()));
            colType.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getAircraftType()));
            colModel.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getModel()));
            colManufacturer.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getManufacturer()));
            colCapacity.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getCapacity())));
            colRange.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getRange())));
            colYear.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getYear())));
            colHours.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.valueOf(c.getValue().getFlightHours())));
            colStatus.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getStatus()));

            colSpecific.setCellValueFactory(c -> {
                Aircraft a = c.getValue();
                if (a instanceof PassengerAircraft p) return new ReadOnlyStringWrapper(p.getCabinClass());
                if (a instanceof CargoAircraft g) return new ReadOnlyStringWrapper(String.valueOf(g.getMaxCargoWeight()));
                if (a instanceof MilitaryAircraft m) return new ReadOnlyStringWrapper(m.getWeaponType());
                return new ReadOnlyStringWrapper("");
            });

            tableAircraft.getColumns().addAll(
                    colId, colType, colModel, colManufacturer,
                    colCapacity, colRange, colYear, colHours,
                    colStatus, colSpecific
            );
        }

        btnRefresh.setOnAction(e -> refreshTable());
        btnAdd.setOnAction(e -> onAdd());
        btnDelete.setOnAction(e -> onDelete());
        btnAnalytics.setOnAction(e -> onAnalytics());

        tableAircraft.setRowFactory(tv -> {
            TableRow<Aircraft> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    showInfo("Aircraft", row.getItem().toString());
                }
            });
            return row;
        });
    }

    // ============================================================
    // УСТАНОВКА СЕРВИСА
    // ============================================================

    /**
     * Устанавливает сервис и настраивает списки.
     *
     * @param service экземпляр {@link AircraftService}
     */
    public void setService(AircraftService service) {
        this.service = service;

        masterList = FXCollections.observableArrayList(service.getAllAircraft());
        filteredList = new FilteredList<>(masterList, p -> true);
        sortedList = new SortedList<>(filteredList);

        sortedList.comparatorProperty().bind(tableAircraft.comparatorProperty());
        tableAircraft.setItems(sortedList);

        setupFilters();
    }

    // ============================================================
    // ФИЛЬТРЫ
    // ============================================================

    /**
     * Настраивает параметры фильтрации.
     * <ul>
     *     <li>Заполняет ComboBox типами;</li>
     *     <li>Добавляет слушатели для обновления фильтров;</li>
     * </ul>
     */
    private void setupFilters() {
        typeFilter.getItems().addAll(
                "All types",
                "Passenger",
                "Cargo",
                "Military"
        );

        typeFilter.getSelectionModel().select("All types");

        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        typeFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
    }

    /**
     * Применяет фильтры по типу и строке поиска.
     */
    private void applyFilters() {

        String search = searchField.getText().toLowerCase().trim();
        String type = typeFilter.getValue();

        filteredList.setPredicate(a -> {

            if (!type.equals("All types")) {
                String t = a.getAircraftType().toLowerCase();

                switch (type) {
                    case "Passenger":
                        if (!t.contains("passenger")) return false;
                        break;
                    case "Cargo":
                        if (!t.contains("cargo")) return false;
                        break;
                    case "Military":
                        if (!t.contains("military")) return false;
                        break;
                }
            }

            if (!search.isEmpty()) {
                return a.getId().toLowerCase().contains(search)
                        || a.getModel().toLowerCase().contains(search)
                        || a.getManufacturer().toLowerCase().contains(search)
                        || a.getStatus().toLowerCase().contains(search);
            }

            return true;
        });
    }

    /**
     * Сбрасывает фильтры поиска.
     */
    @FXML
    private void onClearFilters() {
        searchField.clear();
        typeFilter.getSelectionModel().select("All types");
    }

    // ============================================================
    // ОБНОВЛЕНИЕ
    // ============================================================

    /**
     * Обновляет таблицу, перезагружая данные из сервиса.
     */
    private void refreshTable() {
        if (service == null) return;
        masterList.setAll(service.getAllAircraft());
    }

    // ============================================================
    // ДОБАВЛЕНИЕ
    // ============================================================

    /**
     * Открывает окно добавления самолёта и выполняет валидацию.
     * <p>
     * Возможные ошибки:
     * <ul>
     *     <li>Неверные числовые значения;</li>
     *     <li>Некорректные параметры;</li>
     *     <li>дубликат ID;</li>
     * </ul>
     */
    private void onAdd() {

        if (service == null) return;

        Dialog<Aircraft> dialog = new Dialog<>();
        dialog.setTitle("Добавить самолёт");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));

        ChoiceBox<String> cbType = new ChoiceBox<>();
        cbType.getItems().addAll("Passenger", "Cargo", "Military");
        cbType.setValue("Passenger");

        TextField tfId = new TextField();
        TextField tfModel = new TextField();
        TextField tfManufacturer = new TextField();
        TextField tfCapacity = new TextField();
        TextField tfRange = new TextField();
        TextField tfYear = new TextField();
        TextField tfHours = new TextField();
        TextField tfStatus = new TextField();
        TextField tfSpecific = new TextField();

        tfSpecific.setPromptText("Cabin class / Max cargo / Weapon");

        grid.add(new Label("Type:"), 0, 0);
        grid.add(cbType, 1, 0);

        grid.add(new Label("ID:"), 0, 1);
        grid.add(tfId, 1, 1);

        grid.add(new Label("Model:"), 0, 2);
        grid.add(tfModel, 1, 2);

        grid.add(new Label("Manufacturer:"), 0, 3);
        grid.add(tfManufacturer, 1, 3);

        grid.add(new Label("Capacity:"), 0, 4);
        grid.add(tfCapacity, 1, 4);

        grid.add(new Label("Range:"), 0, 5);
        grid.add(tfRange, 1, 5);

        grid.add(new Label("Year:"), 0, 6);
        grid.add(tfYear, 1, 6);

        grid.add(new Label("Hours:"), 0, 7);
        grid.add(tfHours, 1, 7);

        grid.add(new Label("Status:"), 0, 8);
        grid.add(tfStatus, 1, 8);

        grid.add(new Label("Specific:"), 0, 9);
        grid.add(tfSpecific, 1, 9);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(button -> {

            if (button != ButtonType.OK) return null;

            try {
                String id = tfId.getText().trim();
                String model = tfModel.getText().trim();
                String manufacturer = tfManufacturer.getText().trim();

                int capacity = Integer.parseInt(tfCapacity.getText().trim());
                double range = Double.parseDouble(tfRange.getText().trim());
                int year = Integer.parseInt(tfYear.getText().trim());
                int hours = Integer.parseInt(tfHours.getText().trim());

                String status = tfStatus.getText().trim();
                String specific = tfSpecific.getText().trim();

                return switch (cbType.getValue()) {
                    case "Passenger" -> new PassengerAircraft(
                            id, model, manufacturer, capacity, range, year, hours,
                            status, specific.isEmpty() ? "Economy" : specific
                    );

                    case "Cargo" -> new CargoAircraft(
                            id, model, manufacturer, capacity, range, year, hours,
                            status, Double.parseDouble(specific)
                    );

                    case "Military" -> new MilitaryAircraft(
                            id, model, manufacturer, capacity, range, year, hours,
                            status, specific.isEmpty() ? "-" : specific
                    );

                    default -> null;
                };

            } catch (NumberFormatException nfe) {
                showErrorAlert("Ошибка ввода", "Числовые поля заполнены неверно.");
                return null;

            } catch (Exception e) {
                showErrorAlert("Ошибка", e.getMessage());
                return null;
            }
        });

        dialog.showAndWait().ifPresent(a -> {

            try {
                service.addAircraft(a);
                masterList.add(a);
                applyFilters();

                showInfo("Добавлено", "Самолёт успешно сохранён.");

            } catch (InvalidAircraftDataException | DuplicateIdException | AircraftNotFoundException e) {
                showErrorAlert("Ошибка данных", e.getMessage());

            } catch (Exception e) {
                showErrorAlert("Неизвестная ошибка", e.getMessage());
            }
        });
    }

    // ============================================================
    // УДАЛЕНИЕ
    // ============================================================

    /**
     * Удаляет выбранный самолёт после подтверждения пользователя.
     */
    private void onDelete() {

        Aircraft sel = tableAircraft.getSelectionModel().getSelectedItem();

        if (sel == null) {
            showInfo("Удаление", "Выберите самолёт.");
            return;
        }

        Optional<ButtonType> res = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Удалить самолёт ID=" + sel.getId() + "?",
                ButtonType.YES, ButtonType.NO
        ).showAndWait();

        if (res.isPresent() && res.get() == ButtonType.YES) {
            service.removeAircraft(sel.getId());
            masterList.remove(sel);
        }
    }

    // ============================================================
    // АНАЛИТИКА
    // ============================================================

    /**
     * Открывает окно аналитики и передаёт сервис контроллеру {@link AnalyticsController}.
     */
    private void onAnalytics() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/analytics-view.fxml"));
            Parent root = loader.load();

            AnalyticsController ctrl = loader.getController();
            ctrl.setService(service);

            Stage st = new Stage();
            st.setTitle("Analytics");
            st.setScene(new Scene(root, 900, 600));
            st.initOwner(btnAnalytics.getScene().getWindow());
            st.show();

        } catch (Exception ex) {
            showErrorAlert("Ошибка", "Не удалось открыть окно аналитики: " + ex.getMessage());
        }
    }

    // ============================================================
    // АЛЕРТЫ
    // ============================================================

    /**
     * Показывает окно ошибки.
     *
     * @param title заголовок окна
     * @param msg   текст сообщения
     */
    private void showErrorAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(title);
        a.showAndWait();
    }

    /**
     * Показывает информационное окно.
     *
     * @param title заголовок
     * @param msg   содержимое
     */
    private void showInfo(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(title);
        a.showAndWait();
    }
}
