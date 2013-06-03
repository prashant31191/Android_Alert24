package com.kremski.alert24.tasks;


public class TaskResult<T>  {

	private T result;
	private Exception exception;
	
	public TaskResult(T result) {
		this.result = result;
	}
	
	public TaskResult(Exception e) {
		this.exception = e;
	}
	
	public boolean isExceptionThrown() {
		return exception != null;
	}
	
	public T getResult() {
		return result;
	}
	
	public Exception getException() {
		return exception;
	}
	
}