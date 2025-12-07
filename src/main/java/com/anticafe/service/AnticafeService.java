package com.anticafe.service;

import com.anticafe.model.Table;
import com.anticafe.model.VisitRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис управления антикафе.
 * Обеспечивает основные операции: занятие и освобождение столиков,
 * расчет стоимости посещения, управление ценами.
 *
 * @version 1.0.0
 */
public class AnticafeService {

    private static final Logger logger = LogManager.getLogger(AnticafeService.class);
    private static final int TOTAL_TABLES = 10;

    private final List<Table> tables;
    private final List<VisitRecord> visitHistory;
    private double pricePerMinute;

    /**
     * Создает сервис антикафе с указанной ценой за минуту.
     * Инициализирует все столики в свободном состоянии.
     *
     * @param pricePerMinute стоимость одной минуты пребывания в антикафе
     */
    public AnticafeService(double pricePerMinute) {
        this.pricePerMinute = pricePerMinute;
        this.tables = new ArrayList<>();
        this.visitHistory = new ArrayList<>();
        initializeTables();
        logger.info("Антикафе инициализировано. Цена за минуту: {} руб.", pricePerMinute);
    }

    /**
     * Инициализирует все столики антикафе в свободном состоянии.
     * Создает 10 столиков с номерами от 1 до 10.
     */
    private void initializeTables() {
        for (int i = 1; i <= TOTAL_TABLES; i++) {
            tables.add(new Table(i));
        }
        logger.debug("Создано {} столиков", TOTAL_TABLES);
    }

    /**
     * Возвращает список всех столиков антикафе.
     *
     * @return неизменяемый список столиков
     */
    public List<Table> getTables() {
        return tables;
    }

    /**
     * Возвращает историю всех завершенных посещений.
     * История содержит записи о всех освобожденных столиках с момента запуска.
     *
     * @return список записей посещений
     */
    public List<VisitRecord> getVisitHistory() {
        return visitHistory;
    }

    /**
     * Возвращает текущую стоимость одной минуты пребывания.
     *
     * @return стоимость за минуту в рублях
     */
    public double getPricePerMinute() {
        return pricePerMinute;
    }

    /**
     * Устанавливает новую стоимость за минуту пребывания.
     * Цена не может быть отрицательной.
     *
     * @param pricePerMinute новая стоимость за минуту в рублях
     * @throws IllegalArgumentException если цена отрицательная
     */
    public void setPricePerMinute(double pricePerMinute) {
        if (pricePerMinute < 0) {
            logger.error("Попытка установить отрицательную цену: {}", pricePerMinute);
            throw new IllegalArgumentException("Цена не может быть отрицательной");
        }
        this.pricePerMinute = pricePerMinute;
        logger.info("Установлена новая цена за минуту: {} руб.", pricePerMinute);
    }

    /**
     * Занимает указанный столик для гостей.
     * Устанавливает текущее время как время начала посещения.
     * Если столик уже занят, операция не выполняется.
     *
     * @param tableNumber номер столика (должен быть от 1 до 10)
     * @return true если столик успешно занят, false если столик уже был занят
     * @throws IllegalArgumentException если номер столика выходит за допустимые пределы
     */
    public boolean occupyTable(int tableNumber) {
        validateTableNumber(tableNumber);

        Table table = tables.get(tableNumber - 1);
        if (table.isOccupied()) {
            logger.warn("Столик {} уже занят", tableNumber);
            return false;
        }

        table.occupy();
        logger.info("Столик {} занят гостями", tableNumber);
        return true;
    }

    /**
     * Освобождает указанный столик и создает запись о посещении в истории.
     * Вычисляет продолжительность посещения и итоговую стоимость.
     * Минимальная длительность посещения - 1 минута.
     *
     * @param tableNumber номер столика для освобождения (от 1 до 10)
     * @return стоимость посещения в рублях, или -1 если столик уже был свободен
     * @throws IllegalArgumentException если номер столика выходит за допустимые пределы
     */
    public double releaseTable(int tableNumber) {
        validateTableNumber(tableNumber);

        Table table = tables.get(tableNumber - 1);
        if (!table.isOccupied()) {
            logger.warn("Столик {} уже свободен", tableNumber);
            return -1;
        }

        long minutes = Math.max(1, table.getOccupiedMinutes());
        double cost = calculateCost(minutes);

        VisitRecord record = new VisitRecord(
                tableNumber,
                table.getStartTime(),
                LocalDateTime.now(),
                minutes,
                cost
        );
        visitHistory.add(record);

        table.release();
        logger.info("Столик {} освобожден. Время: {} мин., Сумма: {} руб.",
                tableNumber, minutes, cost);
        return cost;
    }

    /**
     * Вычисляет стоимость посещения за указанное количество минут.
     *
     * @param minutes количество минут пребывания
     * @return стоимость в рублях (минуты умножить на цену за минуту)
     */
    public double calculateCost(long minutes) {
        return minutes * pricePerMinute;
    }

    /**
     * Возвращает объект столика по его номеру.
     *
     * @param tableNumber номер столика (от 1 до 10)
     * @return объект столика
     * @throws IllegalArgumentException если номер столика выходит за допустимые пределы
     */
    public Table getTable(int tableNumber) {
        validateTableNumber(tableNumber);
        return tables.get(tableNumber - 1);
    }

    /**
     * Проверяет корректность номера столика.
     * Номер должен быть в диапазоне от 1 до 10 включительно.
     *
     * @param tableNumber номер столика для проверки
     * @throws IllegalArgumentException если номер столика некорректен
     */
    private void validateTableNumber(int tableNumber) {
        if (tableNumber < 1 || tableNumber > TOTAL_TABLES) {
            logger.error("Некорректный номер столика: {}", tableNumber);
            throw new IllegalArgumentException(
                    "Номер столика должен быть от 1 до " + TOTAL_TABLES
            );
        }
    }

    /**
     * Возвращает общее количество столиков в антикафе.
     *
     * @return количество столиков (всегда 10)
     */
    public int getTotalTables() {
        return TOTAL_TABLES;
    }
}
