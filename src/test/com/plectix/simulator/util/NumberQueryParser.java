package com.plectix.simulator.util;

/*package*/ class NumberQueryParser {	
	public static Piece parse(String str) throws NumberFormatException {
		try {
			return new Piece(Long.parseLong(str));
		} catch (NumberFormatException e) {
			if (str.contains("-")) {
				String[] lines = str.split("-");
				long first = Long.parseLong(lines[0]);
				long second = Long.parseLong(lines[1]);
				return new Piece(first, second);
			} else if (str.contains("=")) {
				String[] lines = str.split("=");
				long first = Long.parseLong(lines[0]);
				long second = Long.parseLong(lines[1]);
				return new Piece(first, second, 2);
			} else {
				throw new NumberFormatException("Bad entry format");
			}
		}
	}
}
