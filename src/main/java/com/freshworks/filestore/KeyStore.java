package com.freshworks.filestore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;
import javax.swing.JFileChooser;
import org.json.JSONObject;
import com.freshworks.filestore.exception.FileStoreException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author MP
 *
 */
public class KeyStore {

	private String homeDir = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
	private double fileSize = 1;
	private short allowedLength = 32;
	private short allowedJsonSize = 16;
	private boolean isFileNew = true;
	private String path;
	private Timer timer;

	/**
	 * Creates file in the default location. Documents in Windows OS
	 * @throws FileStoreException
	 */
	KeyStore() throws FileStoreException {
		this.path = new StringBuffer(homeDir).append("\\").append("KeyStore_").append(generateUniqueFileName())
				.append(".properties").toString();
		try {
			createFile(this.path);
		} catch (IOException e) {
			throw new FileStoreException("File is already created with the same name");
		}
	}

	/**
	 * Creates file in the specified location.
	 * @throws FileStoreException
	 */
	KeyStore(String path) throws FileStoreException {
		this.path = new StringBuffer(path.isEmpty() ? homeDir : path).append("\\").append("KeyStore_")
				.append(generateUniqueFileName()).append(".properties").toString();
		try {
			createFile(this.path);
		} catch (IOException e) {
			throw new FileStoreException("File is already created with the same name");
		}

	}

	private void createFile(String path) throws IOException {
		File file = new File(path);
		file.createNewFile();
	}

	public boolean add(String key, JSONObject value) throws IOException, FileStoreException {
		return write(key, value, 0);
	}

	public boolean add(String key, JSONObject value, long delay) throws IOException, FileStoreException {
		return write(key, value, delay);
	}

	private boolean write(String key, JSONObject value, long delay) throws IOException, FileStoreException {
		checkLength(key);
		key = key.trim();
		checkKey(key);
		BufferedWriter bufferWriter = null;
		boolean isWrittenToFile = false;
		if (fileSize() == this.fileSize) {
			throw new FileStoreException("File size exceeds");
		}
		try {
			checkObjectSize(value);
			FileWriter fileWriter = new FileWriter(path, true);
			bufferWriter = new BufferedWriter(fileWriter);
			if (!isFileNew)
				bufferWriter.newLine();
			bufferWriter.append(key + "=" + value);
			isWrittenToFile = true;
			isFileNew = false;
			if (delay > 0) {
				timer = new Timer();
				timer.schedule(new TtlTask(key, timer), delay * 1000);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			bufferWriter.close();
		}
		return isWrittenToFile;
	}

	/**
	 * Checks the length of the key
	 * 
	 * @param key
	 * @throws FileStoreException
	 */
	private void checkLength(String key) throws FileStoreException {
		if (key == null || key.isEmpty())
			throw new FileStoreException("Invalid key");
		else if (key.trim().length() > allowedLength)
			throw new FileStoreException("key length is exceeded than 32 characters");
	}

	/**
	 * Returns the value from the keystore in String format
	 * 
	 * @param key
	 * @return String
	 * @throws IOException
	 */
	private String load(String key) throws IOException, FileStoreException {
		Properties prop = new Properties();
		try {
			try (InputStream inputStream = new FileInputStream(path)) {
				prop.load(inputStream);
			}
		} catch (IOException e) {
			throw e;
		}
		return prop.getProperty(key);
	}

	/**
	 * Returns the value from the keystore as a JSON object
	 * 
	 * @param key
	 * @return JsonObject
	 * @throws IOException
	 */
	public JsonObject get(String key) throws IOException, FileStoreException {
		validateKey(key);
		String value = load(key);
		JsonElement element = new Gson().fromJson(value, JsonElement.class);
		return element.getAsJsonObject();
	}

	/**
	 * Checks whether the key is already there in the keystore
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void checkKey(String key) throws FileStoreException, IOException {
		if (load(key) != null) {
			throw new FileStoreException("Key is already there");
		}
	}
	
	
	private boolean hasKey(String key) throws FileStoreException, IOException {
		return (load(key) == null)?false:true;
	}

	/**
	 * Writes into file as a property object, particularly useful to write after
	 * delete
	 * 
	 * @param prop
	 * @return
	 */
	private boolean bulkWrite(Properties prop) throws IOException {
		boolean isAdded = false;
		try (OutputStream output = new FileOutputStream(path)) {
			prop.store(output, null);
			isAdded = true;
		} catch (IOException e) {
			throw e;
		}
		return isAdded;
	}

	/**
	 * Removing the key and value from the key store
	 * 
	 * @param key
	 * @throws IOException
	 */
	synchronized public boolean remove(String key) throws IOException, FileStoreException {
		validateKey(key);
		FileReader fileReader = new FileReader(path);
		Properties prop = new Properties();
		prop.load(fileReader);
		prop.remove(key);
		bulkWrite(prop);
		return true;
	}

	/*
	 * TTL task using Timer object
	 */
	class TtlTask extends TimerTask {
		String key;
		Timer timer;

		TtlTask(String key, Timer timer) {
			this.key = key;
			this.timer = timer;
		}

		public void run() {
			try {
				remove(key);
			} catch (IOException | FileStoreException e) {
				e.printStackTrace();
			}
			timer.cancel();
		}
	}

	/**
	 * Generates unique String
	 * 
	 * @return String
	 */
	private String generateUniqueFileName() {
		return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
	}

	/**
	 * Checks the file size in GB
	 * 
	 * @return double
	 * @throws FileNotFoundException
	 */
	private double fileSize() throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists())
			throw new FileNotFoundException();
		return file.length() / (1024 * 1024 * 1024);
	}

	/**
	 * Checks the JSONObject size
	 * 
	 * @param jsonObject
	 * @throws IOException
	 */
	@SuppressWarnings("restriction")
	private void checkObjectSize(JSONObject jsonObject) throws FileStoreException {
		if (ObjectSizeCalculator.getObjectSize(jsonObject) / 1024 > allowedJsonSize) {
			throw new FileStoreException("JSON Size exceeded");
		}
	}
	
	private void validateKey(String key) throws FileStoreException, IOException {
		if (key == null || key.trim().isEmpty())
			throw new FileStoreException("Invalid key");
		else if(!hasKey(key))
			throw new FileStoreException("Key is not present in the file");
	}

	public String getPath() {
		return this.path;
	}
}
