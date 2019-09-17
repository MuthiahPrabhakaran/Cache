package com.freshworks.filestore.controller;

import java.io.IOException;

import org.json.JSONObject;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

public class MainController {

	public static void main(String[] args) {
		KeyStoreWriter fileWriter = new KeyStoreWriter();
        JSONObject j = new JSONObject();
        j.put("1", "SD");
        
        JSONObject jj = new JSONObject();
        jj.put("2", "2dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd2dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd2dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd2dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd2dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd2dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd2dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
       // System.out.println("********** "+ ObjectSizeCalculator.getObjectSize(j));
		
		try {
			//for(int i=0;i<10000;i++) {
				//fileWriter.add("key-"+i, j,5);
			//}
			fileWriter.add("key1", j);
			fileWriter.add("key2", jj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("done");
		
		//fileWriter.delete("key3");
		
	}

}
