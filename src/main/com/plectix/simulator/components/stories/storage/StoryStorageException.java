package com.plectix.simulator.components.stories.storage;

public class StoryStorageException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StoryStorageException(){
		super();
	}

	public StoryStorageException(String string, int index) {
		System.out.println(string);
		System.out.println(index);
	}

	public StoryStorageException(String string) {
		System.out.println(string);
	}

	public StoryStorageException(String string, Long first) {
		System.out.println(string);
		System.out.println(first);
	}
	

}
