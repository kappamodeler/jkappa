package com.plectix.simulator.staticanalysis.stories.storage;

@SuppressWarnings("serial")
public final class StoryStorageException extends Exception {
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
