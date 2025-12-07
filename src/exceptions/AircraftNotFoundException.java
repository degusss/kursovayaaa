package exceptions;

/**
 * Исключение, возникающее в случае, когда самолёт
 * с указанным идентификатором не найден в хранилище.
 *
 * <p>используется в слоях {@code repository} и {@code service},
 * когда поиск по ID не возвращает результата.</p>
 */
public class AircraftNotFoundException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message подробное описание причины ошибки
     */
    public AircraftNotFoundException(String message) {
        super(message);
    }
}
