package com.plectix.simulator.util;

/**
 * 
 * Keeps track of mean and stdev as you go along. Does not store the values. You
 * can ask at any point what the mean and stdev of the added values are. 
 * The stdev formula divides by n-1 (raw score stdev), not n (population stdev).
 * 
 */
final class RunningMetric {
    private String name = null;
    private double n = 0;
    private double sum = 0;
    private double absoluteSum = 0;
    private double sumSquare = 0;
    private double sumCube = 0;
    private double min = Double.POSITIVE_INFINITY;
    private double max = Double.NEGATIVE_INFINITY;

    public RunningMetric() {
        reset();
    }

    public RunningMetric(String name) {
        this.name = name;
        reset();
    }

    /** 
     * Resets - as if no values have been added 
     */
    final void reset() {
        n = sum = absoluteSum = sumSquare = sumCube = 0;
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
    }

    /** 
     * Contributes this new value to the mean and stdev 
     */
    public final void add(double newValue) {
        n++;
        sum += newValue;
        absoluteSum += Math.abs(newValue);
        double square = newValue * newValue;
        sumSquare += square;
        sumCube += (newValue * square);
        if (newValue < min)
            min = newValue;
        if (newValue > max)
            max = newValue;
    }

    /** 
     * Contributes the values from another RunningMetric 
     */
    public final void add(RunningMetric runningMetric) {
        n += runningMetric.n;
        sum += runningMetric.sum;
        absoluteSum += runningMetric.absoluteSum;
        sumSquare += runningMetric.sumSquare;
        sumCube += runningMetric.sumCube;
        if (min > runningMetric.min)
            min = runningMetric.min;
        if (max < runningMetric.max)
            max = runningMetric.max;
    }

    /** 
     * The mean thusfar of all values that have been added 
     */
    final double getAverage() {
        if (n == 0)
            return 0;
        else
            return sum / n;
    }

    
    /** 
     * The mean thusfar of all values that have been added 
     */
    public final double getMean() {
        return getAverage();
    }

    /** 
     * The absolute mean thusfar of all values that have been added 
     */
    public final double getAbsoluteAverage() {
        if (n == 0)
            return 0;
        else
            return absoluteSum / n;
    }

    /** 
     * The variance of all values that have been added (sample variance) 
     */
    final double getVariance() {
        if (n < 2)
            return 0;
        else {
            return (n * sumSquare - sum * sum) / (n * (n - 1)); 
        }
    }

    /** 
     * The standard deviation of all values that have been added 
     */
    public final double getStd() {
        return Math.sqrt(getVariance());
    }

    /** 
     * The skewness of all values that have been added (sample skewness) 
     */
    final double getSkewness() {
        if (n < 3) {
            return 0;
        } else {
        	double std = getStd();
        	if (std == 0.0) {
        		return 0.0;
        	}
        	double average = getAverage();
        	double numerator = sumCube + (2.0*n*average*average - 3.0*sumSquare) * average;
            return (n * numerator) / ((n-1) * (n-2) * std * std * std); 
        }
    }
    
    /** 
     * The minimum of all values that have been added 
     */
    public final double getMin() {
        return min;
    }

    /** 
     * The maximum of all values that have been added 
     */
    public final double getMax() {
        return max;
    }

    /** 
     * The sum of all values that have been added 
     */
    public final double getSum() {
        return sum;
    }

    /** 
     * The number of values added 
     */
    public final double getN() {
        return n;
    }

    /** 
     * A string representation of: name, n(), sum(), min(), max(), mean(), std() 
     */
    @Override
	public final String toString() {
        return name + ": n=" + n + ", sum=" + sum + ", min=" + min + ", max=" + max
                + ", avg=" + getAverage() + ", std=" + getStd() + ", skew=" + getSkewness();
    }
}