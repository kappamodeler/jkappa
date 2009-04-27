package com.plectix.simulator.util;

import com.plectix.simulator.simulator.ThreadLocalData;

public class DecimalFormatter {
	
    public static final String toStringWithSetNumberOfFractionDigits(double d, int numberOfFractionDigits) {
    	return ThreadLocalData.getDecimalFormat(numberOfFractionDigits).format(d);
    }

    public static final String toStringWithSetNumberOfSignificantDigits(double d, int numberOfSignificantDigits) {
    	return findNumberOfSignificantDigits(d, 1, numberOfSignificantDigits);
    }
    
    private static final String findNumberOfSignificantDigits(double d, int upperLimit, int numberOfSignificantDigitsLeft) {
    	if (numberOfSignificantDigitsLeft <= 0) {
    		return ThreadLocalData.getDecimalFormat(0).format(d);
    	}
    	if (d < upperLimit) {
    		return ThreadLocalData.getDecimalFormat(numberOfSignificantDigitsLeft).format(d);
    	}
    	return findNumberOfSignificantDigits(d, 10*upperLimit, numberOfSignificantDigitsLeft-1);
    }
    
}
