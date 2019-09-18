package com.fw.datastore.exception;

public class DataStoreException extends Exception{
	public DataStoreException(){
		super();
	}
	
	public DataStoreException(String message){
		super(message);
	}
	
	public DataStoreException(String message, Exception e){
		super(message, e);
	}
	

}
