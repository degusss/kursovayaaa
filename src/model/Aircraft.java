package model;

/**
 * Абстрактная модель самолёта, содержащая базовые характеристики летательного аппарата.
 * Предоставляет общие поля и методы, которые наследуются специализированными типами
 * самолётов (пассажирскими, грузовыми, военными и т.д.).
 *
 * <p>Класс включает информацию об основном оборудовании:
 * <ul>
 *     <li>ID самолёта;</li>
 *     <li>Модель;</li>
 *     <li>Производитель;</li>
 *     <li>Пассажировместимость;</li>
 *     <li>Дальность полёта;</li>
 *     <li>Год выпуска;</li>
 *     <li>Налёт часов;</li>
 *     <li>Статус эксплуатации.</li>
 * </ul>
 *
 * Каждый конкретный подкласс обязан определять тип самолёта
 * через реализацию метода {@link #getAircraftType()}.
 */
public abstract class Aircraft {

    private String id;
    private String model;
    private String manufacturer;
    private int capacity;
    private double range;
    private int year;
    private int flightHours;
    private String status;

    /**
     * Создаёт объект самолёта с указанными характеристиками.
     *
     * @param id           уникальный ID
     * @param model        модель самолёта
     * @param manufacturer производитель
     * @param capacity     количество пассажиров или максимальная вместимость
     * @param range        максимальная дальность полёта (в километрах)
     * @param year         год выпуска
     * @param flightHours  налёт часов (в часах)
     * @param status       текущий статус самолёта (например, "active", "maintenance", "retired")
     */
    public Aircraft(
            String id,
            String model,
            String manufacturer,
            int capacity,
            double range,
            int year,
            int flightHours,
            String status
    ) {
        this.id = id;
        this.model = model;
        this.manufacturer = manufacturer;
        this.capacity = capacity;
        this.range = range;
        this.year = year;
        this.flightHours = flightHours;
        this.status = status;
    }

    // ---------------------------
    // GETTERS
    // ---------------------------

    /** @return уникальный идентификатор самолёта */
    public String getId() { return id; }

    /** @return модель самолёта */
    public String getModel() { return model; }

    /** @return производитель самолёта */
    public String getManufacturer() { return manufacturer; }

    /** @return вместимость пассажиров или грузовой лимит */
    public int getCapacity() { return capacity; }

    /** @return максимальная дальность полёта */
    public double getRange() { return range; }

    /** @return год выпуска самолёта */
    public int getYear() { return year; }

    /** @return общий налёт часов */
    public int getFlightHours() { return flightHours; }

    /** @return текущий статус самолёта */
    public String getStatus() { return status; }

    // ---------------------------
    // SETTERS
    // ---------------------------

    /** @param id уникальный идентификатор самолёта */
    public void setID(String id) { this.id = id; }

    /** @param model модель самолёта */
    public void setModel(String model) { this.model = model; }

    /** @param manufacturer производитель */
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }

    /** @param capacity вместимость или грузовой лимит */
    public void setCapacity(int capacity) { this.capacity = capacity; }

    /** @param range максимальная дальность полёта */
    public void setRange(double range) { this.range = range; }

    /** @param year год выпуска */
    public void setYear(int year) { this.year = year; }

    /** @param flightHours значение налёта часов */
    public void setFlightHours(int flightHours) { this.flightHours = flightHours; }

    /** @param status статус эксплуатации */
    public void setStatus(String status) { this.status = status; }

    /**
     * Возвращает строковое обозначение типа самолёта.
     * Этот метод должен быть переопределён в подклассах
     * ("Passenger", "Cargo", "Military").
     *
     * @return тип самолёта
     */
    public abstract String getAircraftType();

    // --------------------------
    // CSV экспорт
    // --------------------------

    /**
     * Формирует строку в формате CSV, содержащую основные характеристики самолёта.
     * Поля разделяются символом {@code ;}.
     *
     * @return строка CSV с данными самолёта
     */
    public String toCSV() {
        return String.join(";",
                getAircraftType(),
                id,
                model,
                manufacturer,
                String.valueOf(capacity),
                String.valueOf(range),
                String.valueOf(year),
                String.valueOf(flightHours),
                status
        );
    }

    /**
     * Возвращает форматированное строковое представление самолёта
     * для вывода в консоль или журнал.
     *
     * @return строка с человекочитаемым описанием самолёта
     */
    @Override
    public String toString() {
        return "\n--- " + getAircraftType() + " ---" +
                "\nID: " + id +
                "\nModel: " + model +
                "\nManufacturer: " + manufacturer +
                "\nCapacity: " + capacity +
                "\nRange: " + range +
                "\nYear: " + year +
                "\nFlight hours: " + flightHours +
                "\nStatus: " + status;
    }
}
