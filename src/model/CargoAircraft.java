package model;

/**
 * Модель грузового самолёта. Расширяет базовый класс {@link Aircraft},
 * добавляя характеристику максимальной грузоподъёмности.
 *
 * <p>Используется для представления самолётов, предназначенных
 * для транспортировки различных грузов.</p>
 */
public class CargoAircraft extends Aircraft {

    /** Максимальная грузоподъёмность самолёта (в килограммах). */
    private double maxCargoWeight;

    /**
     * Создаёт объект грузового самолёта с указанными характеристиками
     * и добавляет дополнительную характеристику, зависящую от типа самолета.
     *
     * @param id              ID самолёта
     * @param model           модель самолёта
     * @param manufacturer    производитель
     * @param capacity        базовая вместимость (например, экипаж)
     * @param range           максимальная дальность полёта
     * @param year            год выпуска
     * @param flightHours     общий налёт часов
     * @param status          текущий статус самолёта
     * @param maxCargoWeight  максимальная грузоподъёмность самолёта
     */
    public CargoAircraft(
            String id,
            String model,
            String manufacturer,
            int capacity,
            double range,
            int year,
            int flightHours,
            String status,
            double maxCargoWeight
    ) {
        super(id, model, manufacturer, capacity, range, year, flightHours, status);
        this.maxCargoWeight = maxCargoWeight;
    }

    /**
     * Возвращает максимальную грузоподъёмность самолёта.
     *
     * @return максимальный вес груза, который самолёт способен перевозить
     */
    public double getMaxCargoWeight() {
        return maxCargoWeight;
    }

    /**
     * {@inheritDoc}
     *
     * @return строку "Cargo aircraft"
     */
    @Override
    public String getAircraftType() {
        return "Cargo aircraft";
    }

    /**
     * Формирует строку CSV, включающую базовые параметры самолёта и
     * дополнительное поле {@code maxCargoWeight}.
     *
     * @return CSV-строка с данными грузового самолёта
     */
    @Override
    public String toCSV() {
        return super.toCSV() + ";" + maxCargoWeight;
    }

    /**
     * Возвращает строковое представление грузового самолёта, включая
     * его максимальную грузоподъёмность.
     *
     * @return человекочитаемое описание грузового самолёта
     */
    @Override
    public String toString() {
        return super.toString() + "\nMax cargo weight: " + maxCargoWeight;
    }
}
