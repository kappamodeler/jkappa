package com.plectix.simulator.util;

import com.plectix.simulator.io.xml.SimulationDataXMLWriter;
import com.plectix.simulator.simulator.ThreadLocalData;

public final class DecimalFormatter {
	
    public static final String toStringWithSetNumberOfFractionDigits(double number, int numberOfFractionDigits) {
    	return ThreadLocalData.getDecimalFormat(numberOfFractionDigits).format(number);
    }
    
    public static final String toStringWithSetNumberOfSignificantDigits(double number, int numberOfSignificantDigits) {
    	if (number > 0.0) {
    		return toStringWithSetNumberOfSignificantDigitsForPositiveNumbers(number, numberOfSignificantDigits);
    	} else if (number < 0.0) {
    		return "-" + toStringWithSetNumberOfSignificantDigitsForPositiveNumbers(Math.abs(number), numberOfSignificantDigits);
    	} else {
    		return "0.0"; 
    	}
    }
    
    private static final String toStringWithSetNumberOfSignificantDigitsForPositiveNumbers(double number, int numberOfSignificantDigits) {
    	if (number < 1.0) {
    		if (number < Double.MIN_VALUE) {
    			return "0.0";  // I think returning this should be fine...
    			// This returns extra zeros... But creates a new Formatter for each time we have this small numbers...
    			/* String format = "%." + numberOfSignificantDigits + "G";
    			Formatter fmt = new Formatter();
    			fmt.format(format, number);
    			return fmt.toString();
    			*/
    			
    		}
        	return findNumberOfSignificantDigitsForPositiveNumbersLessThanOne(number, 1, numberOfSignificantDigits);
    	}
    	return findNumberOfSignificantDigitsForPositiveNumbersGreaterThanOne(number, 1, numberOfSignificantDigits);
    }

    private static final String findNumberOfSignificantDigitsForPositiveNumbersLessThanOne(double number, double upperLimit, int numberOfSignificantDigitsLeft) {
    	if (number > upperLimit) {
    		return ThreadLocalData.getDecimalFormat(numberOfSignificantDigitsLeft-1).format(number);
    	}
    	return findNumberOfSignificantDigitsForPositiveNumbersLessThanOne(number, upperLimit/10.0, numberOfSignificantDigitsLeft+1);
    }
    
    private static final String findNumberOfSignificantDigitsForPositiveNumbersGreaterThanOne(double number, int upperLimit, int numberOfSignificantDigitsLeft) {
    	if (numberOfSignificantDigitsLeft <= 0) {
    		return ThreadLocalData.getDecimalFormat(0).format(number);
    	}
    	if (number < upperLimit) {
    		return ThreadLocalData.getDecimalFormat(numberOfSignificantDigitsLeft).format(number);
    	}
    	return findNumberOfSignificantDigitsForPositiveNumbersGreaterThanOne(number, 10*upperLimit, numberOfSignificantDigitsLeft-1);
    }

    public static void main(String[] args) {
    	//TODO o_O
    	
    	System.err.println("0.000000000012345678912 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000000012345678912,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000000000123456789123 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000000123456789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000000001234567891234 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000001234567891234,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000000012345678912345 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000012345678912345,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000000123456789123456 --> " + toStringWithSetNumberOfSignificantDigits( 0.000000123456789123456,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000001234567891234567 --> " + toStringWithSetNumberOfSignificantDigits( 0.000001234567891234567,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000012345678912345678 --> " + toStringWithSetNumberOfSignificantDigits( 0.000012345678912345678,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.000123456789123456789 --> " + toStringWithSetNumberOfSignificantDigits( 0.000123456789123456789,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.001234567891234567891 --> " + toStringWithSetNumberOfSignificantDigits( 0.001234567891234567891,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.012345678912345678912 --> " + toStringWithSetNumberOfSignificantDigits( 0.012345678912345678912,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("0.123456789123456789123 --> " + toStringWithSetNumberOfSignificantDigits( 0.123456789123456789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1 --> " + toStringWithSetNumberOfSignificantDigits( 1,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("10 --> " + toStringWithSetNumberOfSignificantDigits( 10,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("100 --> " + toStringWithSetNumberOfSignificantDigits( 100,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1000 --> " + toStringWithSetNumberOfSignificantDigits( 1000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("10000 --> " + toStringWithSetNumberOfSignificantDigits( 10000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("100000 --> " + toStringWithSetNumberOfSignificantDigits( 100000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1000000 --> " + toStringWithSetNumberOfSignificantDigits( 1000000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("10000000 --> " + toStringWithSetNumberOfSignificantDigits( 10000000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("100000000 --> " + toStringWithSetNumberOfSignificantDigits( 100000000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1000000000 --> " + toStringWithSetNumberOfSignificantDigits( 1000000000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("10000000000 --> " + toStringWithSetNumberOfSignificantDigits( 10000000000.,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));

    	System.err.println("\n\n");

    	System.err.println("1.23456789123 --> " + toStringWithSetNumberOfSignificantDigits( 1.23456789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.2345678912 --> " + toStringWithSetNumberOfSignificantDigits( 1.2345678912,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.234567891 --> " + toStringWithSetNumberOfSignificantDigits( 1.234567891,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.23456789 --> " + toStringWithSetNumberOfSignificantDigits( 1.23456789,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.2345678 --> " + toStringWithSetNumberOfSignificantDigits( 1.2345678,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.234567 --> " + toStringWithSetNumberOfSignificantDigits( 1.234567,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.23456 --> " + toStringWithSetNumberOfSignificantDigits( 1.23456,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.2345 --> " + toStringWithSetNumberOfSignificantDigits( 1.2345,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.234 --> " + toStringWithSetNumberOfSignificantDigits( 1.234,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.23 --> " + toStringWithSetNumberOfSignificantDigits( 1.23,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.2 --> " + toStringWithSetNumberOfSignificantDigits( 1.2,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1.23456789123 --> " + toStringWithSetNumberOfSignificantDigits( 1.23456789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("12.3456789123 --> " + toStringWithSetNumberOfSignificantDigits( 12.3456789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("123.456789123 --> " + toStringWithSetNumberOfSignificantDigits( 123.456789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1234.56789123 --> " + toStringWithSetNumberOfSignificantDigits( 1234.56789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("12345.6789123 --> " + toStringWithSetNumberOfSignificantDigits( 12345.6789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("123456.789123 --> " + toStringWithSetNumberOfSignificantDigits( 123456.789123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1234567.89123 --> " + toStringWithSetNumberOfSignificantDigits( 1234567.89123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("12345678.9123 --> " + toStringWithSetNumberOfSignificantDigits( 12345678.9123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("123456789.123 --> " + toStringWithSetNumberOfSignificantDigits( 123456789.123,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("1234567891.23 --> " + toStringWithSetNumberOfSignificantDigits( 1234567891.23,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("12345678912.3 --> " + toStringWithSetNumberOfSignificantDigits( 12345678912.3,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));

    	System.err.println("\n");

    	System.err.println("-1.E-323/10.0 --> " + toStringWithSetNumberOfSignificantDigits(-1.E-323/10.0,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("+1.E-323/10.0 --> " + toStringWithSetNumberOfSignificantDigits(+1.E-323/10.0,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("-1.E-323 --> " + toStringWithSetNumberOfSignificantDigits(-1.E-323,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("+1.E-323 --> " + toStringWithSetNumberOfSignificantDigits(+1.E-323,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("-1.E-33 --> " + toStringWithSetNumberOfSignificantDigits(-1.E-33,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("+1.E-33 --> " + toStringWithSetNumberOfSignificantDigits(+1.E-33,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("-1.E-3 --> " + toStringWithSetNumberOfSignificantDigits(-1.E-3,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("+1.E-3 --> " + toStringWithSetNumberOfSignificantDigits(+1.E-3,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("-0.0000 --> " + toStringWithSetNumberOfSignificantDigits(-0.0000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    	System.err.println("+0.0000 --> " + toStringWithSetNumberOfSignificantDigits(+0.0000,SimulationDataXMLWriter.NUMBER_OF_SIGNIFICANT_DIGITS));
    }
}
