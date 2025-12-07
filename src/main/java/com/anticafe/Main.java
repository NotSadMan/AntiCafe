package com.anticafe;

import com.anticafe.service.AnticafeService;
import com.anticafe.service.StatisticsService;
import com.anticafe.ui.ConsoleUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Главный класс приложения Антикафе.
 * @version 1.0.0
 */
public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final double DEFAULT_PRICE_PER_MINUTE = 2.0;

    /**
     * Точка входа в приложение.
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        logger.info("Запуск приложения Антикафе");
        try {
            AnticafeService anticafeService = new AnticafeService(DEFAULT_PRICE_PER_MINUTE);
            StatisticsService statisticsService = new StatisticsService(anticafeService);
            ConsoleUI ui = new ConsoleUI(anticafeService, statisticsService);
            ui.start();
        } catch (Exception e) {
            logger.fatal("Критическая ошибка приложения", e);
            System.err.println("Произошла критическая ошибка: " + e.getMessage());
        }
        logger.info("Приложение завершено");
    }
}
