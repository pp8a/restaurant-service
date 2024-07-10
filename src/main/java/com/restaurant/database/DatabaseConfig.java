package com.restaurant.database;

import java.io.InputStream;
import java.util.Collections;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the database configuration and provides methods to load the configuration from a YAML file.
 */
@Getter
@Setter
public class DatabaseConfig {
	private String driver;
	private String url;
	private String username;
	private String password;
	
	/**
     * Loads the database configuration from the YAML file.
     * 
     * @return the loaded DatabaseConfig object
     * @throws DatabaseConfigException if there is an error while loading the configuration
     */
	public static DatabaseConfig load() {
		Yaml yaml = new Yaml(new Constructor(Map.class));
		try (InputStream inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream("application.yml")){
			if(inputStream == null) {
				throw new DatabaseConfigException("YAML file not found in classpath");
			}
			Map<String, Object> yamlMap = yaml.load(inputStream);
			Map<String, String> dbConfig = castToMapStringString(yamlMap.get("database"));
			if(dbConfig == null) {
				throw new DatabaseConfigException("Failed to cast database configuration to Map<String, String>");
			}
			
			return fromYamlMap(dbConfig);
			
		} catch (Exception e) {
			throw new DatabaseConfigException("Failed to load database configuration from YAML file", e);
		}
		
	}

	 /**
     * Creates a DatabaseConfig object from a map.
     * 
     * @param dbConfig the map containing the database configuration
     * @return the DatabaseConfig object
     */
	private static DatabaseConfig fromYamlMap(Map<String, String> dbConfig) {
		DatabaseConfig config = new DatabaseConfig();
		config.setDriver(dbConfig.get("driver"));
		config.setUrl(dbConfig.get("url"));
		config.setUsername(dbConfig.get("username"));
		config.setPassword(dbConfig.get("password"));
		return config;
	}
	
	/**
     * Casts an object to a Map<String, String>.
     * 
     * @param obj the object to cast
     * @return the casted map or an empty map if the cast is not possible
     */
	@SuppressWarnings("unchecked")
	private static Map<String, String> castToMapStringString(Object obj){
		if(obj instanceof Map) {
			Map<?, ?> map = (Map<?, ?>) obj;
			boolean allString =  map.keySet().stream().allMatch(String.class::isInstance) && 
									map.values().stream().allMatch(String.class::isInstance);
									
			if(allString) {
				return (Map<String, String>) map;
			}
		}		
		return Collections.emptyMap();
	}
}
