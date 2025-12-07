package com.anticafe.model;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Модель столика в антикафе.
 * Хранит информацию о номере столика, его текущем статусе (свободен/занят)
 * и времени начала текущего посещения, если столик занят.
 *
 * @version 1.0.0
 */
public class Table {

    private final int number;
    private boolean occupied;
    private LocalDateTime startTime;

    /**
     * Создает новый столик с указанным номером.
     * Изначально столик считается свободным.
     *
     * @param number уникальный номер столика (обычно от 1 до 10)
     */
    public Table(int number) {
        this.number = number;
        this.occupied = false;
        this.startTime = null;
    }

    /**
     * Возвращает номер данного столика.
     *
     * @return номер столика
     */
    public int getNumber() {
        return number;
    }

    /**
     * Проверяет текущий статус столика.
     *
     * @return true, если столик занят, false - если свободен
     */
    public boolean isOccupied() {
        return occupied;
    }

    /**
     * Возвращает время начала текущего посещения.
     *
     * @return объект LocalDateTime с временем начала, или null, если столик свободен
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Переводит столик в статус "занят".
     * Устанавливает текущее системное время как время начала посещения.
     */
    public void occupy() {
        this.occupied = true;
        this.startTime = LocalDateTime.now();
    }

    /**
     * Переводит столик в статус "свободен".
     * Сбрасывает время начала посещения в null.
     */
    public void release() {
        this.occupied = false;
        this.startTime = null;
    }

    /**
     * Вычисляет количество полных минут, прошедших с момента занятия столика.
     * Использует класс Duration для расчета разницы между временем начала и текущим временем.
     *
     * @return количество минут или 0, если столик свободен
     */
    public long getOccupiedMinutes() {
        if (!occupied || startTime == null) {
            return 0;
        }
        return Duration.between(startTime, LocalDateTime.now()).toMinutes();
    }

    /**
     * Вычисляет количество секунд, прошедших с момента занятия столика.
     * Метод может быть полезен для отладки или более точных расчетов.
     *
     * @return количество секунд или 0, если столик свободен
     */
    public long getOccupiedSeconds() {
        if (!occupied || startTime == null) {
            return 0;
        }
        return Duration.between(startTime, LocalDateTime.now()).toSeconds();
    }
}
