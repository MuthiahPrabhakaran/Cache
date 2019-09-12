package com.freshworks.filestore.controller;

import java.io.IOException;
import java.util.Properties;

public class MainController {

	public static void main(String[] args) {
		FileWriter fileWriter = new FileWriter();
        Properties prop = new Properties();
		prop.setProperty("key", "value");
        try {
			System.out.println(fileWriter.createFile("", prop));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
