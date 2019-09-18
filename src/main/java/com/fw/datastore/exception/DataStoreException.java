package com.fw.datastore.exception;

public class DataStoreException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7128055491462545599L;

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
