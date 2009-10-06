package com.plectix.simulator.util;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public class StringBufferWriter extends Writer {
	private final StringBuffer stringBuffer = new StringBuffer();
	
	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		stringBuffer.append(Arrays.copyOfRange(cbuf, off, len));
	}

	@Override
	public final String toString() {
		return stringBuffer.toString();
	}
}
