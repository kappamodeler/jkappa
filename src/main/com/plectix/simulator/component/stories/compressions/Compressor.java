package com.plectix.simulator.component.stories.compressions;

import com.plectix.simulator.component.stories.storage.StoryStorageException;
import com.plectix.simulator.component.stories.storage.WireStorageInterface;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;

public final class Compressor {

	private WireStorageInterface storage;

	public Compressor(WireStorageInterface storage) {
		this.storage = storage;
	}

	public final void execute(StoryCompressionMode compressionMode) throws StoryStorageException {
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

	public final void executeNoneCompression() {
		// TODO Auto-generated method stub
	}

	public final void executeWeakCompression() throws StoryStorageException {
		WeakCompression weak = new WeakCompression(storage);
		weak.process();
	}

	public final void executeStrongCompression() throws StoryStorageException {
		CompressionPassport passport = storage.extractPassport();
		StrongCompression strong = new StrongCompression(passport);
		strong.process();
	}
}
