package ui;

import model.Aircraft;
import model.CargoAircraft;
import model.MilitaryAircraft;
import model.PassengerAircraft;
import service.AircraftService;

import java.util.Scanner;

/**
 * Консольный пользовательский интерфейс для управления системой авиационного парка.
 * <p>
 * Предоставляет меню с командами для:
 * <ul>
 *     <li>Добавления нового самолёта</li>
 *     <li>Просмотра всех самолётов</li>
 *     <li>Поиска самолёта по ID</li>
 *     <li>Удаления самолёта</li>
 *     <li>Просмотра аналитики</li>
 * </ul>
 * <p>
 * Использует {@link AircraftService} как источник данных.
 */
public class ConsoleUI {

    /**
     * Сервис для управления данными о самолётах.
     */
    private final AircraftService service;

    /**
     * Сканер для чтения ввода из консоли.
     */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Создаёт интерфейс, связывая его с сервисом.
     *
     * @param service сервис работы с самолётами
     */
    public ConsoleUI(AircraftService service) {
        this.service = service;
    }

    /**
     * Запускает основное меню консольного приложения.
     * <p>
     * Метод работает в бесконечном цикле, пока пользователь не введёт команду выхода (0).
     */
    public void start() {
        while (true) {
            System.out.println("\n=== МЕНЮ СИСТЕМЫ ===");
            System.out.println("1. Добавить самолёт");
            System.out.println("2. Показать все самолёты");
            System.out.println("3. Найти самолёт по ID");
            System.out.println("4. Удалить самолёт");
            System.out.println("5. Аналитика");
            System.out.println("0. Выход");
            System.out.print("Выберите команду: ");

            int cmd;
            try {
                cmd = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Введите номер команды.");
                continue;
            }

            switch (cmd) {
                case 1 -> addAircraft();
                case 2 -> showAll();
                case 3 -> findAircraft();
                case 4 -> removeAircraft();
                case 5 -> analytics();
                case 0 -> { return; }
                default -> System.out.println("Неизвестная команда.");
            }
        }
    }

    /**
     * Добавляет новый самолёт, запрашивая данные у пользователя.
     * <p>
     * Поддерживаются типы:
     * <ul>
     *     <li>{@link PassengerAircraft} — пассажирский</li>
     *     <li>{@link CargoAircraft} — грузовой</li>
     *     <li>{@link MilitaryAircraft} — военный</li>
     * </ul>
     * <p>
     * В случае ошибки ввода выводит сообщение и прерывает создание.
     */
    private void addAircraft() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine();

            System.out.print("Модель: ");
            String model = scanner.nextLine();

            System.out.print("Производитель: ");
            String manufacturer = scanner.nextLine();

            System.out.print("Вместимость: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            System.out.print("Дальность полёта: ");
            double range = Double.parseDouble(scanner.nextLine());

            System.out.print("Год выпуска: ");
            int year = Integer.parseInt(scanner.nextLine());

            System.out.print("Налёт часов: ");
            int hours = Integer.parseInt(scanner.nextLine());

            System.out.print("Статус (например: 'Рабочий'): ");
            String status = scanner.nextLine();

            System.out.println("\nВыберите тип самолёта:");
            System.out.println("1. Пассажирский");
            System.out.println("2. Карго");
            System.out.println("3. Военный");
            System.out.print("Введите цифру: ");
            int type = Integer.parseInt(scanner.nextLine());

            Aircraft aircraft;

            // PASSENGER
            if (type == 1) {
                System.out.print("Класс кабины (Эконом/Бизнес): ");
                String cabinClass = scanner.nextLine();

                aircraft = new PassengerAircraft(
                        id, model, manufacturer,
                        capacity, range, year, hours,
                        status, cabinClass
                );
            }
            // CARGO
            else if (type == 2) {
                System.out.print("Макс. грузоподъёмность (кг): ");
                double maxCargo = Double.parseDouble(scanner.nextLine());

                aircraft = new CargoAircraft(
                        id, model, manufacturer,
                        capacity, range, year, hours,
                        status, maxCargo
                );
            }
            // MILITARY
            else if (type == 3) {
                System.out.print("Тип вооружения: ");
                String weaponType = scanner.nextLine();

                aircraft = new MilitaryAircraft(
                        id, model, manufacturer,
                        capacity, range, year, hours,
                        status, weaponType
                );
            }
            else {
                System.out.println("Неверный тип!");
                return;
            }

            service.addAircraft(aircraft);
            System.out.println("Самолёт успешно добавлен!");

        } catch (Exception e) {
            System.out.println("Ошибка при вводе данных: " + e.getMessage());
        }
    }

    /**
     * Выводит список всех самолётов, сохранённых в системе.
     */
    private void showAll() {
        System.out.println("\n=== ВСЕ САМОЛЁТЫ ===");
        service.getAllAircraft().forEach(System.out::println);
    }

    /**
     * Ищет самолёт по ID и выводит результат в консоль.
     * <p>
     * Если самолёт не найден, выводится соответствующее сообщение.
     */
    private void findAircraft() {
        System.out.print("Введите ID: ");
        String id = scanner.nextLine();

        Aircraft aircraft = service.findAircraft(id);
        System.out.println(aircraft != null ? aircraft : "Самолёт не найден.");
    }

    /**
     * Удаляет самолёт по ID.
     * <p>
     * Если самолёт найден — он будет удалён.
     * Если нет — выводится сообщение об ошибке.
     */
    private void removeAircraft() {
        System.out.print("Введите ID: ");
        String id = scanner.nextLine();

        if (service.removeAircraft(id)) {
            System.out.println("Удалено.");
        } else {
            System.out.println("Самолёт не найден.");
        }
    }

    /**
     * Выводит простую аналитику по всем самолётам:
     * <ul>
     *     <li>Средняя вместимость</li>
     *     <li>Самый дальнолётный самолёт</li>
     *     <li>Самый старый самолёт</li>
     * </ul>
     */
    private void analytics() {
        System.out.println("\n=== АНАЛИТИКА ===");
        System.out.println("Средняя вместимость: " + service.averageCapacity());
        System.out.println("Самый дальнолётный: " + service.maxRangeAircraft());
        System.out.println("Самый старый: " + service.oldestAircraft());
    }
}
