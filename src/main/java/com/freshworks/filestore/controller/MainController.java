package com.freshworks.filestore.controller;

import java.io.IOException;

import org.json.JSONObject;

public class MainController {

	public static void main(String[] args) {
		KeyStoreWriter fileWriter = new KeyStoreWriter();
        JSONObject j = new JSONObject();
        j.put("1", "2");
		
		try {
			fileWriter.add("key1", j, 10);
			fileWriter.add("key2", j);
			fileWriter.add("key3", j,5);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("done");
		
		//fileWriter.delete("key3");
		
	}

}
