package ui.fx;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.TabPane;
import model.Aircraft;
import service.AircraftService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Контроллер окна аналитики.
 * <p>
 * Построение диаграмм выполняется на основе списка самолётов,
 * предоставляемых {@link AircraftService}. Окно содержит:
 * <ul>
 *     <li>Круговую диаграмму распределения по типам самолётов;</li>
 *     <li>Гистограмму ТОП-10 самолётов по дальности;</li>
 *     <li>Гистограмму ТОП-10 самолётов по вместимости;</li>
 *     <li>Линейный график суммарных часов налёта по годам;</li>
 * </ul>
 * <p>
 * После передачи сервиса методом {@link #setService(AircraftService)}
 * автоматически вызывается построение всех графиков.
 */
public class AnalyticsController {

    /**
     Корневой контейнер вкладок.
     */
    @FXML private TabPane rootPane;

    /**
     Круговая диаграмма типов.
     */
    @FXML private PieChart pieType;

    /**
     Гистограмма дальности.
     */
    @FXML private BarChart<String, Number> barRange;
    @FXML private CategoryAxis rangeXAxis;
    @FXML private NumberAxis rangeYAxis;

    /**
     Гистограмма вместимости.
     */
    @FXML private BarChart<String, Number> barCapacity;
    @FXML private CategoryAxis capXAxis;
    @FXML private NumberAxis capYAxis;

    /**
     Линейный график суммарных часов по годам.
     */
    @FXML private LineChart<String, Number> lineHours;
    @FXML private CategoryAxis yearXAxis;
    @FXML private NumberAxis hoursYAxis;

    /**
     Сервис для получения данных самолётов.
     */
    private AircraftService service;

    /**
     * Цвета, привязанные к типам самолётов.
     * Используются в гистограммах для окраски серий.
     */
    private static final Map<String, String> TYPE_COLOR = Map.of(
            "Passenger Aircraft", "#32CD32",
            "Cargo Aircraft",     "#FFD700",
            "Military Aircraft",  "#FF4500"
    );

    /**
     * Окрашивает отдельную серию BarChart в указанный цвет.
     * <p>
     * Node создаётся асинхронно, поэтому изменение стиля выполняется
     * через listener nodeProperty().
     *
     * @param series серия данных диаграммы
     * @param color CSS-цвет (например, {@code "#FF4500"})
     */
    private void colorSeries(XYChart.Series<String, Number> series, String color) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle("-fx-bar-fill: " + color + ";");
                }
            });
        }
    }

    /**
     * Передаёт сервис в контроллер и инициирует построение всех диаграмм.
     *
     * @param service экземпляр {@link AircraftService}
     */
    public void setService(AircraftService service) {
        this.service = service;
        buildAllCharts();
    }

    /**
     * Перестраивает все диаграммы:
     * <ul>
     *     <li>Pie — распределение по типам;</li>
     *     <li>Bar — дальность;</li>
     *     <li>Bar — вместимость;</li>
     *     <li>Line — часы по годам;</li>
     * </ul>
     */
    private void buildAllCharts() {
        if (service == null) return;

        List<Aircraft> all = service.getAllAircraft();

        buildPieType(all);
        buildTopRange(all);
        buildTopCapacity(all);
        buildHoursByYear(all);
    }

    /**
     * Строит круговую диаграмму с распределением самолётов по типам.
     * На секторе отображается процент от общего количества.
     *
     * @param all список всех самолётов
     */
    private void buildPieType(List<Aircraft> all) {
        pieType.getData().clear();

        long total = all.size();
        if (total == 0) return;

        Map<String, Long> counts = all.stream()
                .collect(Collectors.groupingBy(Aircraft::getAircraftType, Collectors.counting()));

        counts.forEach((type, count) -> {
            double percent = (count * 100.0) / total;
            String label = String.format("%s (%.1f%%)", type, percent);

            PieChart.Data data = new PieChart.Data(label, count);
            pieType.getData().add(data);
        });
    }

    /**
     * Строит гистограмму ТОП-10 самолётов по дальности.
     * <p>
     * Модели разделяются по типам, каждая серия окрашивается в уникальный цвет.
     *
     * @param all список всех самолётов
     */
    private void buildTopRange(List<Aircraft> all) {
        barRange.getData().clear();

        List<Aircraft> top = all.stream()
                .sorted(Comparator.comparingDouble(Aircraft::getRange).reversed())
                .limit(10)
                .toList();

        Map<String, List<Aircraft>> byType = top.stream()
                .collect(Collectors.groupingBy(Aircraft::getAircraftType));

        for (String type : byType.keySet()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(type);

            for (Aircraft a : byType.get(type)) {
                series.getData().add(new XYChart.Data<>(a.getModel(), a.getRange()));
            }

            barRange.getData().add(series);
            colorSeries(series, TYPE_COLOR.getOrDefault(type, "#888"));
        }
    }

    /**
     * Строит гистограмму ТОП-10 самолётов по вместимости.
     * <p>
     * Модели разделяются по типам, серии окрашиваются согласно типу.
     *
     * @param all список всех самолётов
     */
    private void buildTopCapacity(List<Aircraft> all) {
        barCapacity.getData().clear();

        List<Aircraft> top = all.stream()
                .sorted(Comparator.comparingInt(Aircraft::getCapacity).reversed())
                .limit(10)
                .toList();

        Map<String, List<Aircraft>> byType = top.stream()
                .collect(Collectors.groupingBy(Aircraft::getAircraftType));

        for (String type : byType.keySet()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(type);

            for (Aircraft a : byType.get(type)) {
                series.getData().add(new XYChart.Data<>(a.getModel(), a.getCapacity()));
            }

            barCapacity.getData().add(series);
            colorSeries(series, TYPE_COLOR.getOrDefault(type, "#888"));
        }
    }

    /**
     * Строит линейный график общей суммы часов налёта по годам.
     *
     * @param all список всех самолётов
     */
    private void buildHoursByYear(List<Aircraft> all) {
        lineHours.getData().clear();

        Map<Integer, Integer> sumByYear = new TreeMap<>();

        for (Aircraft a : all) {
            sumByYear.merge(a.getYear(), a.getFlightHours(), Integer::sum);
        }

        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.setName("Total Hours");

        for (var e : sumByYear.entrySet()) {
            s.getData().add(new XYChart.Data<>(String.valueOf(e.getKey()), e.getValue()));
        }

        lineHours.getData().add(s);
    }
}
