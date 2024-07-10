package com.restaurant.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Utility class for initializing the database.
 */
public class DatabaseInitializer {
	
	/**
     * Private constructor to prevent instantiation.
     */
	private DatabaseInitializer() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}
	
	/**
     * Initializes the database by executing the specified SQL scripts.
     *
     * @param connection the database connection.
     * @throws SQLException if a database access error occurs.
     * @throws IOException if an I/O error occurs.
     */
	public static void initializeDatabase(Connection connection) throws SQLException, IOException {
        executeSqlScript(connection, "init-ddl.sql");
        executeSqlScript(connection, "init-dml.sql");
    }
	
	/**
     * Executes the specified SQL script.
     *
     * @param connection the database connection.
     * @param scriptPath the path to the SQL script.
     * @throws SQLException if a database access error occurs.
     * @throws IOException if an I/O error occurs.
     */	
	private static void executeSqlScript(Connection connection, String scriptPath) throws SQLException, IOException {
        String sqlScript = getSql(scriptPath);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlScript);
        }
    }
	
	/**
     * Reads the SQL script from the specified resource.
     *
     * @param resourceName the name of the resource.
     * @return the SQL script as a String.
     * @throws IOException if an I/O error occurs.
     */
	private static String getSql(final String resourceName) throws IOException {
        InputStream inputStream = DatabaseInitializer.class.getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new IOException("Script file not found: " + resourceName);
        }
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}
