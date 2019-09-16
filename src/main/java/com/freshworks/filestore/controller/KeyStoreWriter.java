package com.freshworks.filestore.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFileChooser;

public class KeyStoreWriter {

	String homeDir = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
	String path;

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

	public void createFile(String path) {
		File file = new File(path);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean setProperty(String key, String value, long delay) throws IOException {
		BufferedWriter bufferWriter = null;
		boolean isWrittenToFile = false;
		try {
			FileWriter fileWriter = new FileWriter(path, true);
			bufferWriter = new BufferedWriter(fileWriter);
			bufferWriter.append(key + "=" + value);
			bufferWriter.newLine();
			isWrittenToFile = true;
			
			if(delay>0) {
				scheduleDeletion(delay*1000, key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			bufferWriter.close();
		}
		return isWrittenToFile;
	}
	
	
	public void scheduleDeletion(long delay, final String key) {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				delete(key);
				System.out.println("scheduled deletion");
				//Cannot refer to the non-final local variable key defined in an enclosing scope
			}
		};
		
		Timer timer = new Timer();
		timer.schedule(task, delay);
	}
	
	public String loadFile(String key) {
		Properties prop = new Properties();
		try {
			try (InputStream inputStream = new FileInputStream(path)) {
				/*
				 * if(inputStream == null) { throw new FileNotFoundException("File Not Found");
				 * }
				 */
				prop.load(inputStream);
				System.out.println(prop.getProperty(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop.getProperty(key);
	}

	public boolean delete(String key) {
		boolean isDeleted = false;
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(path));
			prop.remove(key);
			writeToFile(prop);
			isDeleted = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return isDeleted;

	}

	public void writeToFile(Properties prop) {
		try (OutputStream output = new FileOutputStream(path)) {
			prop.store(output, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String generateUniqueFileName() {
		long millis = System.currentTimeMillis();
		String datetime = new Date().toString();
		datetime = datetime.replace(" ", "");
		datetime = datetime.replace(":", "");
		return datetime + "_" + millis;
	}

}
