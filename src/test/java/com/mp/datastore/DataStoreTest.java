package com.mp.datastore;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.mp.datastore.exception.DataStoreException;
import com.mp.datastore.impl.DataStoreImpl;

import junit.framework.Assert;

public class DataStoreTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	static DataStore dataStore;
	static JSONObject jsonObject;
	String employeeDetails = "employeeDetails";

	@BeforeClass
	public static void initializeVariables() {
		try {
			dataStore = new DataStoreImpl();
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
		jsonObject = new JSONObject();
		jsonObject.put("address", "Chennai");
		jsonObject.put("country", "India");
	}

	@Test
	public void testCreateFileWithoutDefinedPath() {
		File file = new File(dataStore.getPath());
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testCreateFileWithDefinedPath() {
		DataStoreImpl dataStoreNew = null;
		try {
			dataStoreNew = new DataStoreImpl(new JFileChooser().getFileSystemView().getDefaultDirectory().toString());
		} catch (DataStoreException e) {
			e.printStackTrace();
		}
		File file = new File(dataStoreNew.getPath());
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testAdd() {
		try {
			Assert.assertTrue(dataStore.add("employee3", jsonObject));
			Assert.assertEquals(jsonObject.toString(), dataStore.get("employee3").toString());
		} catch (IOException | DataStoreException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddWithTTL() {
		try {
			Assert.assertTrue(dataStore.add("employee2", jsonObject, 10));
			Assert.assertEquals(jsonObject.toString(), dataStore.get("employee2").toString());
			Thread.sleep(10000);
			dataStore.get("employee2");
		} catch (IOException | DataStoreException | InterruptedException e) {
			assertInvalidKey(e);
		}
	}

	@Test
	public void testRemove() {
		try {
			Assert.assertTrue(dataStore.add("employee1", jsonObject));
			Assert.assertTrue(dataStore.remove("employee1"));
			dataStore.get("employee1");
		} catch (IOException | DataStoreException e) {
			assertInvalidKey(e);
		}
	}

	@Test
	public void testWithEmptyKey() {
		try {
			dataStore.add("", jsonObject);
		} catch (IOException | DataStoreException e) {
			Assert.assertTrue(e instanceof DataStoreException);
			Assert.assertEquals("Invalid key", e.getMessage());
		}
	}

	@Test
	public void testWithNullKey() {
		try {
			dataStore.add(null, jsonObject);
		} catch (IOException | DataStoreException e) {
			Assert.assertTrue(e instanceof DataStoreException);
			Assert.assertEquals("Invalid key", e.getMessage());
		}
	}

	@Test
	public void testWithKeyHas33Chars() {
		try {
			dataStore.add("testTestTestTesttestTestTestTestT", jsonObject);
		} catch (IOException | DataStoreException e) {
			Assert.assertTrue(e instanceof DataStoreException);
			Assert.assertEquals("key length is exceeded than 32 characters", e.getMessage());
		}
	}

	@Test
	public void testWithDuplicateKey() {
		try {
			dataStore.add(employeeDetails, jsonObject);
		} catch (IOException | DataStoreException e) {
			Assert.assertTrue(e instanceof DataStoreException);
			Assert.assertEquals("Key is already there", e.getMessage());
		}
	}

	@Test
	public void testLoadWithInvalidKey() {
		try {
			dataStore.get("Invalid Key");
		} catch (DataStoreException | IOException e) {
			assertInvalidKey(e);
		}
	}
	
	@Test
	public void testLoadWithNullKey() {
		try {
			dataStore.get(null);
		} catch (DataStoreException | IOException e) {
			assertEmptyKey(e);
		}
	}
	
	@Test
	public void testLoadWithEmptyKey() {
		try {
			dataStore.get(" ");
		} catch (DataStoreException | IOException e) {
			assertEmptyKey(e);
		}
	}
	
	
	@Test
	public void testRemoveWithInvalidKey() {
		try {
			dataStore.remove("Invalid Key");
		} catch (DataStoreException | IOException e) {
			assertInvalidKey(e);
		}
	}
	
	@Test
	public void testRemoveWithNullKey() {
		try {
			dataStore.remove(null);
		} catch (DataStoreException | IOException e) {
			assertEmptyKey(e);
		}
	}
	
	@Test
	public void testRemoveWithEmptyKey() {
		try {
			dataStore.remove(" ");
		} catch (DataStoreException | IOException e) {
			assertEmptyKey(e);
		}
	}
	
	public void assertInvalidKey(Exception e) {
		Assert.assertTrue(e instanceof DataStoreException);
		Assert.assertEquals("Key is not present in the file", e.getMessage());
	}
	
	public void assertEmptyKey(Exception e) {
		Assert.assertTrue(e instanceof DataStoreException);
		Assert.assertEquals("Invalid key", e.getMessage());
	}
}
