package com.plectix.simulator.util.io;

import java.io.IOException;
import java.io.Reader;

public class StringBufferReader extends Reader {
	private final char[] data;
	private int numberSymbolsRead = 0;
	
	public StringBufferReader(String source) {
		data = source.toCharArray();
	}
	
	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub

	}

	private final boolean allSymbolsRead() {
		return numberSymbolsRead > data.length - 1;
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if (allSymbolsRead()) {
			return -1;
		}
		
		for (int i = 0; i < len; i++) {
			if (allSymbolsRead()) {
				return i;
			}
			cbuf[off + i] = data[numberSymbolsRead++];
		}
		return len;
	}

}
