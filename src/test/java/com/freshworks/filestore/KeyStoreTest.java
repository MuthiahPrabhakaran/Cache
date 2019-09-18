package com.freshworks.filestore;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.freshworks.filestore.exception.FileStoreException;

import junit.framework.Assert;

public class KeyStoreTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	static KeyStore keyStore;
	static JSONObject jsonObject;
	String employeeDetails = "employeeDetails";

	@BeforeClass
	public static void initializeVariables() {
		try {
			keyStore = new KeyStore();
		} catch (FileStoreException e) {
			e.printStackTrace();
		}
		jsonObject = new JSONObject();
		jsonObject.put("address", "Chennai");
		jsonObject.put("country", "India");
	}

	@Test
	public void testCreateFileWithoutDefinedPath() {
		File file = new File(keyStore.getPath());
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testCreateFileWithDefinedPath() {
		KeyStore keyStoreNew = null;
		try {
			keyStoreNew = new KeyStore(new JFileChooser().getFileSystemView().getDefaultDirectory().toString());
		} catch (FileStoreException e) {
			e.printStackTrace();
		}
		File file = new File(keyStoreNew.getPath());
		Assert.assertTrue(file.exists());
	}

	@Test
	public void testAdd() {
		try {
			Assert.assertTrue(keyStore.add(employeeDetails, jsonObject));
			Assert.assertEquals(jsonObject.toString(), keyStore.get(employeeDetails).toString());
		} catch (IOException | FileStoreException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testAddWithTTL() {
		try {
			Assert.assertTrue(keyStore.add("employee2", jsonObject, 10));
			Assert.assertEquals(jsonObject.toString(), keyStore.get("employee2").toString());
			Thread.sleep(10000);
			keyStore.get("employee2");
		} catch (IOException | FileStoreException | InterruptedException e) {
			assertInvalidKey(e);
		}
	}

	@Test
	public void testRemove() {
		try {
			Assert.assertTrue(keyStore.add("employee1", jsonObject));
			Assert.assertTrue(keyStore.remove("employee1"));
			keyStore.get("employee1");
		} catch (IOException | FileStoreException e) {
			assertInvalidKey(e);
		}
	}

	@Test
	public void testWithEmptyKey() {
		try {
			keyStore.add("", jsonObject);
		} catch (IOException | FileStoreException e) {
			Assert.assertTrue(e instanceof FileStoreException);
			Assert.assertEquals("Invalid key", e.getMessage());
		}
	}

	@Test
	public void testWithNullKey() {
		try {
			keyStore.add(null, jsonObject);
		} catch (IOException | FileStoreException e) {
			Assert.assertTrue(e instanceof FileStoreException);
			Assert.assertEquals("Invalid key", e.getMessage());
		}
	}

	@Test
	public void testWithKeyHas33Chars() {
		try {
			keyStore.add("testTestTestTesttestTestTestTestT", jsonObject);
		} catch (IOException | FileStoreException e) {
			Assert.assertTrue(e instanceof FileStoreException);
			Assert.assertEquals("key length is exceeded than 32 characters", e.getMessage());
		}
	}

	@Test
	public void testWithDuplicateKey() {
		try {
			keyStore.add(employeeDetails, jsonObject);
		} catch (IOException | FileStoreException e) {
			Assert.assertTrue(e instanceof FileStoreException);
			Assert.assertEquals("Key is already there", e.getMessage());
		}
	}

	@Test
	public void testLoadWithInvalidKey() {
		try {
			keyStore.get("Invalid Key");
		} catch (FileStoreException | IOException e) {
			assertInvalidKey(e);
		}
	}
	
	@Test
	public void testLoadWithNullKey() {
		try {
			keyStore.get(null);
		} catch (FileStoreException | IOException e) {
			assertEmptyKey(e);
		}
	}
	
	@Test
	public void testLoadWithEmptyKey() {
		try {
			keyStore.get(" ");
		} catch (FileStoreException | IOException e) {
			assertEmptyKey(e);
		}
	}
	
	
	@Test
	public void testRemoveWithInvalidKey() {
		try {
			keyStore.remove("Invalid Key");
		} catch (FileStoreException | IOException e) {
			assertInvalidKey(e);
		}
	}
	
	@Test
	public void testRemoveWithNullKey() {
		try {
			keyStore.remove(null);
		} catch (FileStoreException | IOException e) {
			assertEmptyKey(e);
		}
	}
	
	@Test
	public void testRemoveWithEmptyKey() {
		try {
			keyStore.remove(" ");
		} catch (FileStoreException | IOException e) {
			assertEmptyKey(e);
		}
	}
	
	public void assertInvalidKey(Exception e) {
		Assert.assertTrue(e instanceof FileStoreException);
		Assert.assertEquals("Key is not present in the file", e.getMessage());
	}
	
	public void assertEmptyKey(Exception e) {
		Assert.assertTrue(e instanceof FileStoreException);
		Assert.assertEquals("Invalid key", e.getMessage());
	}
}
