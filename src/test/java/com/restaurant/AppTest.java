package com.restaurant;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

/**
 * Test class for the {@link App} class.
 */
class AppTest {

    private ListAppender<ILoggingEvent> listAppender;

    /**
     * Sets up the test environment by configuring the logger to capture log events.
     */
    @BeforeEach
    public void setUp() {
        Logger logger = (Logger) LoggerFactory.getLogger(App.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    /**
     * Tears down the test environment by stopping the list appender.
     */
    @AfterEach
    public void tearDown() {
        listAppender.stop();
    }

    /**
     * Tests the main method of the {@link App} class to ensure it logs the expected message.
     */
    @Test
    void testMain() {
        App.main(new String[]{});

        List<ILoggingEvent> logEvents = listAppender.list;
        boolean logFound = logEvents.stream()
            .anyMatch(event -> event.getLevel() == Level.INFO && event.getFormattedMessage().contains("Welcome to the restaurant service"));

        assertTrue(logFound, "Expected log message not found");
    }
}