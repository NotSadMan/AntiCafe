package com.anticafe.ui;

import com.anticafe.model.Table;
import com.anticafe.service.AnticafeService;
import com.anticafe.service.StatisticsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Консольный интерфейс пользователя для управления антикафе.
 * Предоставляет меню для всех операций с антикафе.
 *
 * @version 1.0.0
 */
public class ConsoleUI {

    private static final Logger logger = LogManager.getLogger(ConsoleUI.class);

    private final AnticafeService anticafeService;
    private final StatisticsService statisticsService;
    private final Scanner scanner;
    private boolean running;

    /**
     * Создает консольный интерфейс.
     *
     * @param anticafeService    сервис антикафе
     * @param statisticsService  сервис статистики
     */
    public ConsoleUI(AnticafeService anticafeService, StatisticsService statisticsService) {
        this.anticafeService = anticafeService;
        this.statisticsService = statisticsService;
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    /**
     * Запускает главный цикл интерфейса.
     */
    public void start() {
        logger.info("Запуск консольного интерфейса");
        System.out.println("=== Добро пожаловать в систему управления Антикафе ===");

        while (running) {
            printMenu();
            processCommand();
        }

        scanner.close();
        logger.info("Завершение работы интерфейса");
    }

    /**
     * Выводит главное меню.
     */
    private void printMenu() {
        System.out.println("\n--- ГЛАВНОЕ МЕНЮ ---");
        System.out.println("1. Посадить гостей за столик");
        System.out.println("2. Освободить столик");
        System.out.println("3. Текущая статистика");
        System.out.println("4. Архивная статистика");
        System.out.println("5. Изменить цену за минуту");
        System.out.println("6. Показать статус всех столиков");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    /**
     * Обрабатывает команду пользователя.
     */
    private void processCommand() {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> occupyTableAction();
                case 2 -> releaseTableAction();
                case 3 -> showCurrentStatistics();
                case 4 -> showArchiveStatistics();
                case 5 -> changePriceAction();
                case 6 -> showAllTablesStatus();
                case 0 -> exitAction();
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        } catch (InputMismatchException e) {
            logger.warn("Некорректный ввод пользователя", e);
            System.out.println("Ошибка: введите число!");
            scanner.nextLine();
        }
    }

    /**
     * Обрабатывает действие посадки гостей.
     */
    private void occupyTableAction() {
        showFreeTables();
        System.out.print("Введите номер столика (1-10): ");

        try {
            int tableNumber = scanner.nextInt();
            scanner.nextLine();

            if (anticafeService.occupyTable(tableNumber)) {
                System.out.println("Гости успешно посажены за столик " + tableNumber);
            } else {
                System.out.println("Столик " + tableNumber + " уже занят!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите корректный номер столика!");
            scanner.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Показывает список свободных столиков.
     */
    private void showFreeTables() {
        System.out.print("Свободные столики: ");
        StringBuilder free = new StringBuilder();
        for (Table table : anticafeService.getTables()) {
            if (!table.isOccupied()) {
                if (!free.isEmpty()) {
                    free.append(", ");
                }
                free.append(table.getNumber());
            }
        }
        System.out.println(!free.isEmpty() ? free.toString() : "нет свободных");
    }

    /**
     * Обрабатывает действие освобождения столика.
     */
    private void releaseTableAction() {
        showOccupiedTables();
        System.out.print("Введите номер столика для освобождения (1-10): ");

        try {
            int tableNumber = scanner.nextInt();
            scanner.nextLine();

            double cost = anticafeService.releaseTable(tableNumber);
            if (cost >= 0) {
                System.out.printf("Столик %d освобожден. К оплате: %.2f руб.%n",
                        tableNumber, cost);
            } else {
                System.out.println("Столик " + tableNumber + " уже свободен!");
            }
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите корректный номер столика!");
            scanner.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Показывает список занятых столиков.
     */
    private void showOccupiedTables() {
        System.out.print("Занятые столики: ");
        StringBuilder occupied = new StringBuilder();
        for (Table table : anticafeService.getTables()) {
            if (table.isOccupied()) {
                if (!occupied.isEmpty()) {
                    occupied.append(", ");
                }
                occupied.append(table.getNumber());
            }
        }
        System.out.println(!occupied.isEmpty() ? occupied.toString() : "нет занятых");
    }

    /**
     * Показывает текущую статистику.
     */
    private void showCurrentStatistics() {
        logger.info("Просмотр текущей статистики");
        System.out.println("\n=== ТЕКУЩАЯ СТАТИСТИКА ===");
        System.out.printf("Цена за минуту: %.2f руб.%n", anticafeService.getPricePerMinute());
        System.out.printf("Занято столиков: %d из %d%n",
                statisticsService.getOccupiedTablesCount(),
                anticafeService.getTotalTables());

        System.out.println("\nЗанятые столики:");
        boolean hasOccupied = false;

        for (Table table : anticafeService.getTables()) {
            if (table.isOccupied()) {
                hasOccupied = true;
                long minutes = Math.max(1, table.getOccupiedMinutes());
                double cost = anticafeService.calculateCost(minutes);
                System.out.printf("  Столик %d: %d мин., к оплате: %.2f руб.%n",
                        table.getNumber(), minutes, cost);
            }
        }

        if (!hasOccupied) {
            System.out.println("  Нет занятых столиков");
        }

        System.out.printf("%nОбщая сумма к оплате (если все уйдут сейчас): %.2f руб.%n",
                statisticsService.getCurrentTotalCost());
    }

    /**
     * Показывает архивную статистику.
     */
    private void showArchiveStatistics() {
        logger.info("Просмотр архивной статистики");
        System.out.println("\n=== АРХИВНАЯ СТАТИСТИКА ===");

        double totalEarnings = statisticsService.getTotalEarnings();
        double avgTime = statisticsService.getAverageOccupationTime();
        int popularTable = statisticsService.getMostPopularTable();
        int profitableTable = statisticsService.getMostProfitableTable();

        System.out.printf("Общий заработок: %.2f руб.%n", totalEarnings);
        System.out.printf("Среднее время занятости столика: %.1f мин.%n", avgTime);

        if (popularTable > 0) {
            System.out.printf("Самый популярный столик: %d (выбран %d раз)%n",
                    popularTable,
                    statisticsService.getTableVisitCount(popularTable));
        } else {
            System.out.println("Самый популярный столик: нет данных");
        }

        if (profitableTable > 0) {
            System.out.printf("Самый доходный столик: %d (принес %.2f руб.)%n",
                    profitableTable,
                    statisticsService.getTableTotalEarnings(profitableTable));
        } else {
            System.out.println("Самый доходный столик: нет данных");
        }

        System.out.printf("Всего посещений: %d%n",
                anticafeService.getVisitHistory().size());

        showVisitHistory();
    }

    /**
     * Обрабатывает изменение цены.
     */
    private void changePriceAction() {
        System.out.printf("Текущая цена: %.2f руб./мин.%n",
                anticafeService.getPricePerMinute());
        System.out.print("Введите новую цену за минуту: ");

        try {
            double newPrice = scanner.nextDouble();
            scanner.nextLine();
            anticafeService.setPricePerMinute(newPrice);
            System.out.printf("Цена изменена на %.2f руб./мин.%n", newPrice);
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите корректное число!");
            scanner.nextLine();
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    /**
     * Показывает детальную историю посещений.
     * Использует методы getStartTime() и getEndTime() из VisitRecord.
     */
    private void showVisitHistory() {
        var history = anticafeService.getVisitHistory();
        if (history.isEmpty()) {
            return;
        }

        System.out.println("\n--- Последние посещения ---");
        int count = Math.min(5, history.size()); // показываем последние 5
        for (int i = history.size() - count; i < history.size(); i++) {
            var record = history.get(i);
            System.out.printf("  Столик %d: %s - %s (%d мин., %.2f руб.)%n",
                    record.getTableNumber(),
                    record.getStartTime().toLocalTime(),
                    record.getEndTime().toLocalTime(),
                    record.getDurationMinutes(),
                    record.getTotalCost());
        }
    }

    /**
     * Показывает статус всех столиков.
     */
    private void showAllTablesStatus() {
        System.out.println("\n=== СТАТУС СТОЛИКОВ ===");
        for (int i = 1; i <= anticafeService.getTotalTables(); i++) {
            Table table = anticafeService.getTable(i);
            String status = table.isOccupied() ? "ЗАНЯТ" : "свободен";
            System.out.printf("Столик %2d: %s", table.getNumber(), status);

            if (table.isOccupied()) {
                long minutes = Math.max(1, table.getOccupiedMinutes());
                long seconds = table.getOccupiedSeconds() % 60;
                double cost = anticafeService.calculateCost(minutes);
                System.out.printf(" (%d мин. %d сек., %.2f руб.)", minutes, seconds, cost);
            }
            System.out.println();
        }
    }

    /**
     * Обрабатывает выход из программы.
     */
    private void exitAction() {
        System.out.println("Завершение работы...");
        running = false;
    }
}
