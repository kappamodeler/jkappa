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
			/*
			IEventIterator it = storage.extractPassport().eventIterator(false);

			int n = 0;
			
			while (it.hasNext())
			{
				it.next();
				//System.out.println(it.value().getStepId() + "\t" + it.value().getRuleId() + "\t*" + it.value().getMark().toString() + "*");
				n++;
			}
			System.out.println("\n>>> " + n);
			*/
			
			weak.process();
			
			storage.extractPassport().removeEventWithMarkDelete();
			
			/*
			
			it = storage.extractPassport().eventIterator(false);

			n = 0;
			
			while (it.hasNext())
			{
				it.next();
				//System.out.println(it.value().getStepId() + "\t" + it.value().getRuleId() + "\t*" + it.value().getMark().toString() + "*");
				n++;
			}
			System.out.println(">>> " + n);
			
			IEventIterator it = storage.extractPassport().eventIterator(false);
			
			while (it.hasNext())
			{
				it.next();
				System.out.println(it.value().getStepId() + "\t" + it.value().getRuleId() + "\t*" + it.value().getMark().toString() + "*");
			}
			*/
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
			WeakCompression weak = new WeakCompression(storage);
			
			IEventIterator it = storage.extractPassport().eventIterator(false);

			int n = 0;
			
			while (it.hasNext())
			{
				it.next();
				//System.out.println(it.value().getStepId() + "\t" + it.value().getRuleId() + "\t*" + it.value().getMark().toString() + "*");
				n++;
			}
			System.out.println("\n>>> " + n);
			
			weak.process();
			
			storage.extractPassport().removeEventWithMarkDelete();
			
			it = storage.extractPassport().eventIterator(false);

			n = 0;
			
			while (it.hasNext())
			{
				it.next();
				//System.out.println(it.value().getStepId() + "\t" + it.value().getRuleId() + "\t*" + it.value().getMark().toString() + "*");
				n++;
			}
			System.out.println(">>> " + n);
			*/
			
			strong.process();
			storage.extractPassport().removeEventWithMarkDelete();

			/*
			it = storage.extractPassport().eventIterator(false);

			n = 0;
			
			while (it.hasNext())
			{
				it.next();
				//System.out.println(it.value().getStepId() + "\t" + it.value().getRuleId() + "\t*" + it.value().getMark().toString() + "*");
				n++;
			}
			System.out.println(">>> " + n);
			*/
		} catch (StoryStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void doCompression ()
	{

	}
}
