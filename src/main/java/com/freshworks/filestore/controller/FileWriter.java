package com.freshworks.filestore.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Properties;

import javax.swing.JFileChooser;

public class FileWriter {

	String homeDir = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();

	public String createFile(String path, Properties prop) throws IOException {
		path = path.isEmpty() ? homeDir : path;
		path = new StringBuffer(path).append("\\").append("KeyStore_").append(generateUniqueFileName())
				.append(".properties").toString();
		File file = new File(path);
		if (!file.createNewFile()) {
			throw new IOException("File already exists");
		}
		try (OutputStream output = new FileOutputStream(path)) {
			prop.store(output, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public String generateUniqueFileName() {
		long millis = System.currentTimeMillis();
		String datetime = new Date().toString();
		datetime = datetime.replace(" ", "");
		datetime = datetime.replace(":", "");
		return datetime + "_" + millis;
	}

}
