package com.plectix.simulator.components.stories.compressions;

import com.plectix.simulator.components.stories.storage.AbstractStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.simulator.SimulationArguments;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;

public class Compressor {
	
	private AbstractStorage storage;
	
	
	
	public Compressor(AbstractStorage storage) {	
		//System.out.println("doCompression, guys!");
		this.storage = storage;		
	}

	public void execute(StoryCompressionMode compressionMode) {
		switch (compressionMode) {
		case NONE:
			executeNoneCompression();
			return;
		case WEAK:
			executeWeakCompression();
			return;
		case STRONG:
			executeStrongCompression();
			return;
		}
		
	}

	public void executeNoneCompression() {
		// TODO Auto-generated method stub
		
	}

	public void executeWeakCompression() {
		WeakCompression weak = new WeakCompression(storage);
		
		try {
			weak.process();
		} catch (StoryStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executeStrongCompression() {
		// TODO Auto-generated method stub
		
	}

	
	public void doCompression ()
	{

	}
}
