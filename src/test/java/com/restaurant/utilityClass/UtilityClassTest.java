package com.restaurant.utilityClass;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.restaurant.controllers.ApiPaths;
import com.restaurant.dao.DAOUtils;
import com.restaurant.queries.OrderApprovalSQLQueries;
import com.restaurant.queries.OrderDetailSQLQueries;
import com.restaurant.queries.OrderStatusSQLQueries;
import com.restaurant.queries.ProductCategorySQLQueries;
import com.restaurant.queries.ProductSQLQueries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class to verify that utility classes cannot be instantiated.
 */
class UtilityClassTest {
	
	/**
     * Tests that the specified class cannot be instantiated.
     *
     * @param clazz the class to test.
     */
	@ParameterizedTest
    @MethodSource("provideClasses")
    void testClassCannotBeInstantiated(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            constructor.setAccessible(true);
            Throwable exception = assertThrows(InvocationTargetException.class, () -> {
                constructor.newInstance();
            }).getCause();
            assertTrue(exception instanceof UnsupportedOperationException, 
            		"Expected UnsupportedOperationException");
        }
    }
	
	 /**
     * Provides the classes to be tested.
     *
     * @return a stream of classes.
     */
	private static Stream<Class<?>> provideClasses() {
        return Stream.of(
            ProductSQLQueries.class,
            ProductCategorySQLQueries.class,
            OrderStatusSQLQueries.class,
            OrderDetailSQLQueries.class,
            OrderApprovalSQLQueries.class,
            ApiPaths.class,
            DAOUtils.class
        );
    }
}