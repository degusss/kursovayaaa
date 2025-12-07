package model;

/**
 * Модель военного самолёта. Расширяет {@link Aircraft}, добавляя информацию
 * о типе вооружения, которым оснащён самолёт.
 *
 * <p>Используется для представления боевой авиации различного назначения.</p>
 */
public class MilitaryAircraft extends Aircraft {

    /** Тип вооружения, установленный на самолёте. */
    private String weaponType;

    /**
     * Создаёт объект военного самолёта с указанными характеристиками.
     * и добавляет доп.характеристику зависящую от типа самолета.
     *
     * @param id            уникальный идентификатор самолёта
     * @param model         модель самолёта
     * @param manufacturer  производитель
     * @param capacity      вместимость (например, количество мест экипажа)
     * @param range         максимальная дальность полёта
     * @param year          год выпуска
     * @param flightHours   общий налёт часов
     * @param status        текущий статус эксплуатации
     * @param weaponType    тип вооружения самолёта
     */
    public MilitaryAircraft(
            String id,
            String model,
            String manufacturer,
            int capacity,
            double range,
            int year,
            int flightHours,
            String status,
            String weaponType
    ) {
        super(id, model, manufacturer, capacity, range, year, flightHours, status);
        this.weaponType = weaponType;
    }

    /**
     * Возвращает тип вооружения самолёта.
     *
     * @return тип вооружения
     */
    public String getWeaponType() {
        return weaponType;
    }

    /**
     * {@inheritDoc}
     *
     * @return строку "Military aircraft"
     */
    @Override
    public String getAircraftType() {
        return "Military aircraft";
    }

    /**
     * Формирует CSV-строку, содержащую данные военного самолёта,
     * включая тип вооружения.
     *
     * @return CSV-представление объекта
     */
    @Override
    public String toCSV() {
        return super.toCSV() + ";" + weaponType;
    }

    /**
     * Возвращает подробное строковое описание самолёта,
     * включая информацию о вооружении.
     *
     * @return человекочитаемое описание самолёта
     */
    @Override
    public String toString() {
        return super.toString() + "\nWeapon type: " + weaponType;
    }
}
