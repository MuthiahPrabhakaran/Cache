package com.freshworks.filestore.exception;

public class FileStoreException extends Exception{
	public FileStoreException(){
		super();
	}
	
	public FileStoreException(String message){
		super(message);
	}
	
	public FileStoreException(String message, Exception e){
		super(message, e);
	}
	

}
