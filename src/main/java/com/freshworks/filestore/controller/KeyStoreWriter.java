package com.freshworks.filestore.controller;

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
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;

import org.json.JSONObject;

import com.google.gson.Gson;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

public class KeyStoreWriter {

	String homeDir = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
	double fileSize = 10;
	short allowedLength = 32;
	short allowedJsonSize = 1;
	boolean isFileNew = true;
	String path;
	Timer timer;

	KeyStoreWriter() {
		this.path = new StringBuffer(homeDir).append("\\").append("KeyStore_").append(generateUniqueFileName())
				.append(".properties").toString();
		createFile(path);
	}

	KeyStoreWriter(String path) {
		this.path = new StringBuffer(path.isEmpty() ? homeDir : path).append("\\").append("KeyStore_")
				.append(generateUniqueFileName()).append(".properties").toString();
		createFile(path);
	}

	/**
	 * @param path
	 *            creates file in specified location if no location is given, it
	 *            will create in documents directory on Windows OS
	 */
	public void createFile(String path) {
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean add(String key, JSONObject value) throws IOException {
		return write(key, value, 0);
	}

	public boolean add(String key, JSONObject value, long delay) throws IOException {
		return write(key, value, delay);
	}

	private boolean write(String key, JSONObject value, long delay) throws IOException {
		checkLength(key);
		key = key.trim();
		hasKey(key);
		BufferedWriter bufferWriter = null;
		boolean isWrittenToFile = false;
		if (fileSize() == this.fileSize) {
			throw new IOException("File size exceeds");
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
			e.printStackTrace();
		} finally {
			bufferWriter.close();
		}
		return isWrittenToFile;
	}

	private void checkLength(String key) throws IOException {
		key = key.trim();
		if (key == null)
			throw new IOException("Invalid key");
		else if (key.length() > allowedLength)
			throw new IOException("key length is exceeded");
	}

	public String load(String key) {
		Properties prop = new Properties();
		try {
			try (InputStream inputStream = new FileInputStream(path)) {
				if (inputStream == null) {
					throw new FileNotFoundException("File Not Found");
				}
				prop.load(inputStream);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(new Gson().fromJson(prop.getProperty(key), JSONObject.class));
		return prop.getProperty(key);
	}

	private void hasKey(String key) throws IOException {
		if (load(key) != null) {
			throw new IOException("Key is already there");
		}
	}

	private boolean bulkWrite(Properties prop) {
		boolean isAdded = false;
		try (OutputStream output = new FileOutputStream(path)) {
			prop.store(output, null);
			isAdded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isAdded;
	}

	public void remove(String key) throws IOException {
		FileReader fileReader = new FileReader(path);
		Properties prop = new Properties();
		prop.load(fileReader);
		prop.remove(key);
		bulkWrite(prop);
	}

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
			} catch (IOException e) {
				e.printStackTrace();
			}
			timer.cancel();
		}
	}

	private String generateUniqueFileName() {
		long millis = System.currentTimeMillis();
		String datetime = new Date().toString();
		datetime = datetime.replace(" ", "");
		datetime = datetime.replace(":", "");
		return datetime + "_" + millis;
	}

	private double fileSize() throws FileNotFoundException {
		File file = new File(path);
		if (!file.exists())
			throw new FileNotFoundException();
		System.out.println(file.length());
		return file.length() / (1024 * 1024 * 1024);
	}

	private void checkObjectSize(JSONObject jsonObject) throws IOException {

		if (ObjectSizeCalculator.getObjectSize(jsonObject) / 1024 > allowedJsonSize) {
			throw new IOException("JSON Size exceeded");
		}

	}
}
