package com.plectix.simulator.util;

import com.plectix.simulator.simulator.SimulationData;
import com.plectix.simulator.simulator.ThreadLocalData;

public final class DecimalFormatter {
	
    public static final String toStringWithSetNumberOfFractionDigits(double number, int numberOfFractionDigits) {
    	return ThreadLocalData.getDecimalFormat(numberOfFractionDigits).format(number);
    }

    public static final String toStringWithSetNumberOfSignificantDigits(double number, int numberOfSignificantDigits) {
    	if (number < 1.0) {
        	return findNumberOfSignificantDigitsForNumbersLessThanOne(number, 1, numberOfSignificantDigits);
    	}
    	return findNumberOfSignificantDigits(number, 1, numberOfSignificantDigits);
    }

    private static final String findNumberOfSignificantDigitsForNumbersLessThanOne(double number, double upperLimit, int numberOfSignificantDigitsLeft) {
    	if (number > upperLimit) {
    		return ThreadLocalData.getDecimalFormat(numberOfSignificantDigitsLeft-1).format(number);
    	}
    	return findNumberOfSignificantDigitsForNumbersLessThanOne(number, upperLimit/10.0, numberOfSignificantDigitsLeft+1);
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

    public static void main(String[] args) {
    	System.err.println("0.000000000012345678912 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000000012345678912, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000000000123456789123 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000000123456789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000000001234567891234 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000001234567891234, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000000012345678912345 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000012345678912345, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000000123456789123456 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000123456789123456, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000001234567891234567 --> " + toStringWithSetNumberOfSignificantDigits( 0.000001234567891234567, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000012345678912345678 --> " + toStringWithSetNumberOfSignificantDigits( 0.000012345678912345678, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000123456789123456789 --> " + toStringWithSetNumberOfSignificantDigits( 0.000123456789123456789, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.001234567891234567891 --> " + toStringWithSetNumberOfSignificantDigits( 0.001234567891234567891, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.012345678912345678912 --> " + toStringWithSetNumberOfSignificantDigits( 0.012345678912345678912, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.123456789123456789123 --> " + toStringWithSetNumberOfSignificantDigits( 0.123456789123456789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1 --> " + toStringWithSetNumberOfSignificantDigits( 1, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("10 --> " + toStringWithSetNumberOfSignificantDigits( 10, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("100 --> " + toStringWithSetNumberOfSignificantDigits( 100, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1000 --> " + toStringWithSetNumberOfSignificantDigits( 1000, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("10000 --> " + toStringWithSetNumberOfSignificantDigits( 10000, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("100000 --> " + toStringWithSetNumberOfSignificantDigits( 100000, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1000000 --> " + toStringWithSetNumberOfSignificantDigits( 1000000, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("10000000 --> " + toStringWithSetNumberOfSignificantDigits( 10000000, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("100000000 --> " + toStringWithSetNumberOfSignificantDigits( 100000000, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1000000000 --> " + toStringWithSetNumberOfSignificantDigits( 1000000000, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("10000000000 --> " + toStringWithSetNumberOfSignificantDigits( 10000000000., SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));

    	System.err.println("\n\n");

    	System.err.println("1.23456789123 --> " + toStringWithSetNumberOfSignificantDigits( 1.23456789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.2345678912 --> " + toStringWithSetNumberOfSignificantDigits( 1.2345678912, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.234567891 --> " + toStringWithSetNumberOfSignificantDigits( 1.234567891, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.23456789 --> " + toStringWithSetNumberOfSignificantDigits( 1.23456789, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.2345678 --> " + toStringWithSetNumberOfSignificantDigits( 1.2345678, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.234567 --> " + toStringWithSetNumberOfSignificantDigits( 1.234567, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.23456 --> " + toStringWithSetNumberOfSignificantDigits( 1.23456, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.2345 --> " + toStringWithSetNumberOfSignificantDigits( 1.2345, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.234 --> " + toStringWithSetNumberOfSignificantDigits( 1.234, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.23 --> " + toStringWithSetNumberOfSignificantDigits( 1.23, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.2 --> " + toStringWithSetNumberOfSignificantDigits( 1.2, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.23456789123 --> " + toStringWithSetNumberOfSignificantDigits( 1.23456789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("12.3456789123 --> " + toStringWithSetNumberOfSignificantDigits( 12.3456789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("123.456789123 --> " + toStringWithSetNumberOfSignificantDigits( 123.456789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1234.56789123 --> " + toStringWithSetNumberOfSignificantDigits( 1234.56789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("12345.6789123 --> " + toStringWithSetNumberOfSignificantDigits( 12345.6789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("123456.789123 --> " + toStringWithSetNumberOfSignificantDigits( 123456.789123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1234567.89123 --> " + toStringWithSetNumberOfSignificantDigits( 1234567.89123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("12345678.9123 --> " + toStringWithSetNumberOfSignificantDigits( 12345678.9123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("123456789.123 --> " + toStringWithSetNumberOfSignificantDigits( 123456789.123, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1234567891.23 --> " + toStringWithSetNumberOfSignificantDigits( 1234567891.23, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("12345678912.3 --> " + toStringWithSetNumberOfSignificantDigits( 12345678912.3, SimulationData.NUMBER_OF_SIGNIFICANT_DIGITS));

    }
}
