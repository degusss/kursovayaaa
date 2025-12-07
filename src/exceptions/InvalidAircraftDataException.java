package exceptions;

/**
 * Исключение, выбрасываемое при предоставлении некорректных,
 * неполных или противоречивых данных при создании или изменении
 * объекта {@link model.Aircraft}.
 *
 * <p>Используется для валидации данных при загрузке из CSV,
 * при вводе пользователем или при создании объекта.</p>
 */
public class InvalidAircraftDataException extends RuntimeException {

    /**
     * Создаёт исключение с указанным описанием ошибки.
     *
     * @param message подробная информация о некорректных данных
     */
    public InvalidAircraftDataException(String message) {
        super(message);
    }
}
