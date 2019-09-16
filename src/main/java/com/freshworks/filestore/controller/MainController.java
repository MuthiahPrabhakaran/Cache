package com.freshworks.filestore.controller;

import java.io.IOException;

public class MainController {

	public static void main(String[] args) {
		KeyStoreWriter fileWriter = new KeyStoreWriter();
       
		/*Properties prop = new Properties();
		prop.setProperty("key", "value");
        try {
			System.out.println(fileWriter.createFile("", prop));
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		try {
			fileWriter.setProperty("key3","value3", 0);
			fileWriter.setProperty("key4","value",10);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		fileWriter.loadFile("key3");
		System.out.println("done");
		
		//fileWriter.delete("key3");
		
	}

}
