package com.plectix.simulator.util;

import com.plectix.simulator.simulator.ThreadLocalData;

public final class DecimalFormatter {
	
    public static final String toStringWithSetNumberOfFractionDigits(double number, int numberOfFractionDigits) {
    	return ThreadLocalData.getDecimalFormat(numberOfFractionDigits).format(number);
    }

    public static final String toStringWithSetNumberOfSignificantDigits(double number, int numberOfSignificantDigits) {
    	return findNumberOfSignificantDigits(number, 1, numberOfSignificantDigits);
    }
    
    private static final String findNumberOfSignificantDigits(double number, int upperLimit, int numberOfSignificantDigitsLeft) {
    	if (numberOfSignificantDigitsLeft <= 0) {
    		return ThreadLocalData.getDecimalFormat(0).format(number);
    	}
    	if (number < upperLimit) {
    		return ThreadLocalData.getDecimalFormat(numberOfSignificantDigitsLeft).format(number);
    	}
    	return findNumberOfSignificantDigits(number, 10*upperLimit, numberOfSignificantDigitsLeft-1);
    }
    
}
