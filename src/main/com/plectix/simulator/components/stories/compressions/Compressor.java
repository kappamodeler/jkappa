package com.plectix.simulator.components.stories.compressions;

import com.plectix.simulator.components.stories.storage.IWireStorage;
import com.plectix.simulator.components.stories.storage.StoryStorageException;
import com.plectix.simulator.simulator.SimulationArguments.StoryCompressionMode;

public class Compressor {
	
	private IWireStorage storage;
	
	
	
	public Compressor(IWireStorage storage) {	
		this.storage = storage;		
	}

	public void execute(StoryCompressionMode compressionMode) {
		switch (compressionMode) {
		case NONE:
			executeNoneCompression();
//			System.out.println("NONE");
			return;
		case WEAK:
			executeWeakCompression();
//			System.out.println("WEAK");
			return;
		case STRONG:
			executeStrongCompression();
//			System.out.println("STRONG");
			return;
		}
		
	}

	public void executeNoneCompression() {
		// TODO Auto-generated method stub
		
	}

	public void executeWeakCompression() {
		WeakCompression weak = new WeakCompression(storage);
		
		try {
			//System.out.println("\n>>> " + storage.extractPassport().getAllEventsByNumber().size());
			
			weak.process();
			
			/*
			it = storage.extractPassport().eventIterator(false);

			while (it.hasNext())
			{
				it.next();
				System.out.println(it.value().getStepId() + "\t" + it.value().getRuleId() + "\t*" + it.value().getMark().toString() + "*");
				//for (WireHashKey wk: it.value().getAtomicEvents().keySet())
				//	System.out.println("\t" + wk.toString());
			}
			*/
			
			//System.out.println("\n>>> " + storage.extractPassport().getAllEventsByNumber().size());
		} catch (StoryStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executeStrongCompression() {
		CompressionPassport passport = storage.extractPassport();
		StrongCompression strong = new StrongCompression(passport);
		
		try {
			/*
			System.out.println("\n>>> " + storage.extractPassport().getAllEventsByNumber().size());

			WeakCompression weak = new WeakCompression(storage);
			weak.process();
			
			System.out.println("\n>>> " + storage.extractPassport().getAllEventsByNumber().size());
			*/
			
			strong.process();

			//System.out.println("\n>>> " + storage.extractPassport().getAllEventsByNumber().size());
		} catch (StoryStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void doCompression ()
	{

	}
}
