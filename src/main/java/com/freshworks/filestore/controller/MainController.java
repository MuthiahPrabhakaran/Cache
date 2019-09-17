package com.freshworks.filestore.controller;

import java.io.IOException;

import org.json.JSONObject;

public class MainController {

	public static void main(String[] args) {
		KeyStoreWriter fileWriter = new KeyStoreWriter();
        JSONObject j = new JSONObject();
        j.put("1", "2");
		
		try {
			for(int i=0;i<10000;i++) {
				fileWriter.add("key-"+i, j,5);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("done");
		
		//fileWriter.delete("key3");
		
	}

}
