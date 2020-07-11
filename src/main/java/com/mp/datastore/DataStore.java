package com.mp.datastore;

import java.io.IOException;

import org.json.JSONObject;

import com.mp.datastore.exception.DataStoreException;
import com.google.gson.JsonObject;

public interface DataStore {

	boolean add(String key, JSONObject value) throws IOException, DataStoreException;

	boolean add(String key, JSONObject value, long delay) throws IOException, DataStoreException;

	/**
	 * Returns the value from the keystore as a JSON object
	 * 
	 * @param key
	 * @return JsonObject
	 * @throws IOException
	 */
	JsonObject get(String key) throws IOException, DataStoreException;

	/**
	 * Removing the key and value from the key store
	 * 
	 * @param key
	 * @throws IOException
	 */
	boolean remove(String key) throws IOException, DataStoreException;
	
	String getPath();
}