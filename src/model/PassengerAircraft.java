package model;

/**
 * Модель пассажирского самолёта. Расширяет {@link Aircraft},
 * добавляя характеристику класса обслуживания (кабина).
 *
 * <p>Предназначена для представления воздушных судов гражданской
 * авиации, использующихся для перевозки пассажиров.</p>
 */
public class PassengerAircraft extends Aircraft {

    /** Класс кабины самолёта, например: Эконом, Бизнес. */
    private String cabinClass;

    /**
     * Создаёт объект пассажирского самолёта с указанными характеристиками.
     *
     * @param id            уникальный идентификатор самолёта
     * @param model         модель самолёта
     * @param manufacturer  производитель
     * @param capacity      количество пассажирских мест
     * @param range         максимальная дальность полёта
     * @param year          год выпуска
     * @param flightHours   общий налёт часов
     * @param status        текущий статус самолёта
     * @param cabinClass    класс обслуживания (например, Economy, Business)
     */
    public PassengerAircraft(
            String id,
            String model,
            String manufacturer,
            int capacity,
            double range,
            int year,
            int flightHours,
            String status,
            String cabinClass
    ) {
        super(id, model, manufacturer, capacity, range, year, flightHours, status);
        this.cabinClass = cabinClass;
    }

    /**
     * Возвращает класс обслуживания пассажирского самолёта.
     *
     * @return класс кабины (например, Эконом или Бизнес)
     */
    public String getCabinClass() {
        return cabinClass;
    }

    /**
     * {@inheritDoc}
     *
     * @return строку "Passenger aircraft"
     */
    @Override
    public String getAircraftType() {
        return "Passenger aircraft";
    }

    /**
     * Формирует CSV-строку, содержащую данные пассажирского самолёта,
     * включая класс кабины.
     *
     * @return CSV-представление объекта
     */
    @Override
    public String toCSV() {
        return super.toCSV() + ";" + cabinClass;
    }

    /**
     * Возвращает строковое описание объекта, дополняя его информацией
     * о классе кабины.
     *
     * @return человекочитаемое описание самолёта
     */
    @Override
    public String toString() {
        return super.toString() + "\nCabin class: " + cabinClass;
    }
}
