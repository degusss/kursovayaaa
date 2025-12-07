package exceptions;

/**
 * Исключение, выбрасываемое при попытке добавить самолёт
 * с ID, который уже существует в системе.
 *
 * <p>Используется для предотвращения конфликтов ID
 * в репозиториях и сервисах.</p>
 */
public class DuplicateIdException extends RuntimeException {

    /**
     * Создаёт исключение с указанным сообщением.
     *
     * @param message описание ошибки, с указанием конфликта ID
     */
    public DuplicateIdException(String message) {
        super(message);
    }
}
