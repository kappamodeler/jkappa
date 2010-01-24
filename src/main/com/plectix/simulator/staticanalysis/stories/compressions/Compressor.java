package com.plectix.simulator.staticanalysis.stories.compressions;

import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;
import com.plectix.simulator.staticanalysis.stories.storage.StoryStorageException;
import com.plectix.simulator.staticanalysis.stories.storage.WireStorageInterface;

public final class Compressor {

	private final WireStorageInterface storage;

	public Compressor(WireStorageInterface storage) {
		this.storage = storage;
	}

	public final void execute(StoryCompressionMode compressionMode)
			throws StoryStorageException {
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

	final void executeNoneCompression() {
		// TODO Auto-generated method stub
	}

	final void executeWeakCompression() throws StoryStorageException {
		WeakCompression weak = new WeakCompression(storage);
		weak.process();
	}

	final void executeStrongCompression() throws StoryStorageException {
		CompressionPassport passport = storage.extractPassport();
		StrongCompression strong = new StrongCompression(passport);
		strong.process();
	}
}
