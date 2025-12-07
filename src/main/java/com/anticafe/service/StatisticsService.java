package com.anticafe.service;

import com.anticafe.model.Table;
import com.anticafe.model.VisitRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис статистики антикафе.
 * Предоставляет методы для получения текущей и архивной статистики
 * по работе заведения: занятость столиков, заработок, популярность.
 *
 * @version 1.0.0
 */
public class StatisticsService {

    private static final Logger logger = LogManager.getLogger(StatisticsService.class);
    private final AnticafeService anticafeService;

    /**
     * Создает экземпляр сервиса статистики.
     *
     * @param anticafeService сервис антикафе, предоставляющий доступ к данным
     */
    public StatisticsService(AnticafeService anticafeService) {
        this.anticafeService = anticafeService;
    }

    /**
     * Возвращает количество занятых столиков в данный момент.
     * Подсчитывает столики, у которых установлен флаг занятости.
     *
     * @return количество занятых столиков (от 0 до 10)
     */
    public int getOccupiedTablesCount() {
        return (int) anticafeService.getTables().stream()
                .filter(Table::isOccupied)
                .count();
    }

    /**
     * Вычисляет общую сумму, которую гости должны заплатить, если покинут антикафе прямо сейчас.
     * Сумма рассчитывается для всех занятых столиков на основе текущего времени пребывания.
     *
     * @return общая текущая сумма к оплате в рублях
     */
    public double getCurrentTotalCost() {
        double total = 0;
        for (Table table : anticafeService.getTables()) {
            if (table.isOccupied()) {
                long minutes = Math.max(1, table.getOccupiedMinutes());
                total += anticafeService.calculateCost(minutes);
            }
        }
        logger.debug("Текущая сумма со всех столиков: {} руб.", total);
        return total;
    }

    /**
     * Возвращает общий заработок антикафе за всё время работы приложения.
     * Суммирует стоимость всех завершенных посещений из истории.
     *
     * @return общий заработок в рублях
     */
    public double getTotalEarnings() {
        double total = anticafeService.getVisitHistory().stream()
                .mapToDouble(VisitRecord::getTotalCost)
                .sum();
        logger.debug("Общий заработок: {} руб.", total);
        return total;
    }

    /**
     * Вычисляет среднее время занятости столика за всю историю посещений.
     * Рассчитывается как среднее арифметическое продолжительности всех визитов.
     *
     * @return среднее время в минутах или 0, если история посещений пуста
     */
    public double getAverageOccupationTime() {
        List<VisitRecord> history = anticafeService.getVisitHistory();
        if (history.isEmpty()) {
            return 0;
        }
        double average = history.stream()
                .mapToLong(VisitRecord::getDurationMinutes)
                .average()
                .orElse(0);
        logger.debug("Среднее время занятости: {} мин.", average);
        return average;
    }

    /**
     * Определяет номер самого популярного столика.
     * Популярным считается столик, который выбирали наибольшее количество раз.
     *
     * @return номер самого популярного столика или -1, если история посещений пуста
     */
    public int getMostPopularTable() {
        Map<Integer, Integer> tableVisits = getTableVisitCounts();
        if (tableVisits.isEmpty()) {
            return -1;
        }

        int mostPopular = tableVisits.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);

        logger.debug("Самый популярный столик: {}", mostPopular);
        return mostPopular;
    }

    /**
     * Формирует карту с количеством посещений для каждого столика.
     * Ключ карты - номер столика, значение - количество посещений.
     *
     * @return карта (Map) со статистикой посещений по столикам
     */
    public Map<Integer, Integer> getTableVisitCounts() {
        Map<Integer, Integer> counts = new HashMap<>();
        for (VisitRecord record : anticafeService.getVisitHistory()) {
            counts.merge(record.getTableNumber(), 1, Integer::sum);
        }
        return counts;
    }

    /**
     * Определяет номер столика, который принес наибольший доход.
     * Суммирует стоимость всех посещений для каждого столика и выбирает максимальную.
     *
     * @return номер самого доходного столика или -1, если история посещений пуста
     */
    public int getMostProfitableTable() {
        Map<Integer, Double> tableEarnings = getTableEarnings();
        if (tableEarnings.isEmpty()) {
            return -1;
        }

        int mostProfitable = tableEarnings.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);

        logger.debug("Самый доходный столик: {}", mostProfitable);
        return mostProfitable;
    }

    /**
     * Формирует карту с суммарным доходом для каждого столика.
     * Ключ карты - номер столика, значение - общий доход с этого столика.
     *
     * @return карта (Map) со статистикой доходов по столикам
     */
    public Map<Integer, Double> getTableEarnings() {
        Map<Integer, Double> earnings = new HashMap<>();
        for (VisitRecord record : anticafeService.getVisitHistory()) {
            earnings.merge(record.getTableNumber(), record.getTotalCost(), Double::sum);
        }
        return earnings;
    }

    /**
     * Возвращает количество посещений для конкретного столика.
     *
     * @param tableNumber номер столика
     * @return количество посещений указанного столика (0, если посещений не было)
     */
    public int getTableVisitCount(int tableNumber) {
        return getTableVisitCounts().getOrDefault(tableNumber, 0);
    }

    /**
     * Возвращает общий доход, полученный с конкретного столика.
     *
     * @param tableNumber номер столика
     * @return сумма дохода с указанного столика в рублях
     */
    public double getTableTotalEarnings(int tableNumber) {
        return getTableEarnings().getOrDefault(tableNumber, 0.0);
    }
}
