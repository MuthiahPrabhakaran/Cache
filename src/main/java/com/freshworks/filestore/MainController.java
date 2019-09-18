package com.freshworks.filestore;

import java.io.IOException;

import org.json.JSONObject;

import com.freshworks.filestore.exception.FileStoreException;

import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator;

public class MainController {

	public static void main(String[] args) throws FileStoreException {
		KeyStore fileWriter = new KeyStore();
        JSONObject j = new JSONObject();
        j.put("1", "SD");
        
        JSONObject jj = new JSONObject();
        jj.put("2", "sdf");
       // System.out.println("********** "+ ObjectSizeCalculator.getObjectSize(j));
		
		try {
			//for(int i=0;i<10000;i++) {
				//fileWriter.add("key-"+i, j,5);
			//}
			fileWriter.add("key1", j);
			fileWriter.get("sdfsdf");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("done");
		
		//fileWriter.delete("key3");
		
	}

}
