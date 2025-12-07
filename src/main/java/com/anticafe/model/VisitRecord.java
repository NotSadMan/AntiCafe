package com.anticafe.model;

import java.time.LocalDateTime;

/**
 * Модель записи о завершенном посещении столика.
 * Хранит неизменяемую информацию об истории посещения:
 * какой столик был занят, время начала и конца, продолжительность и стоимость.
 * Используется для формирования архивной статистики.
 *
 * @author Anticafe Team
 * @version 1.0.0
 */
public class VisitRecord {

    private final int tableNumber;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final long durationMinutes;
    private final double totalCost;

    /**
     * Создает новую запись о посещении.
     * Все поля инициализируются при создании и не могут быть изменены позже.
     *
     * @param tableNumber     номер столика, который был занят
     * @param startTime       дата и время начала посещения
     * @param endTime         дата и время окончания посещения
     * @param durationMinutes фактическая продолжительность посещения в минутах
     * @param totalCost       итоговая стоимость посещения в рублях
     */
    public VisitRecord(int tableNumber,
                       LocalDateTime startTime,
                       LocalDateTime endTime,
                       long durationMinutes,
                       double totalCost) {
        this.tableNumber = tableNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = durationMinutes;
        this.totalCost = totalCost;
    }

    /**
     * Возвращает номер столика, к которому относится данная запись.
     *
     * @return номер столика
     */
    public int getTableNumber() {
        return tableNumber;
    }

    /**
     * Возвращает дату и время, когда столик был занят.
     *
     * @return время начала посещения
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Возвращает дату и время, когда столик был освобожден.
     *
     * @return время окончания посещения
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Возвращает продолжительность посещения в полных минутах.
     *
     * @return длительность в минутах
     */
    public long getDurationMinutes() {
        return durationMinutes;
    }

    /**
     * Возвращает итоговую стоимость данного посещения.
     *
     * @return стоимость в рублях
     */
    public double getTotalCost() {
        return totalCost;
    }
}
