package com.plectix.simulator.util;

/**
 * 
 * Keeps track of mean and stdev as you go along. Does not store the values. You
 * can ask at any point what the mean and stdev of the added values are. 
 * The stdev formula divides by n-1 (raw score stdev), not n (population stdev).
 * 
 */
public class RunningMetric {
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

    public String getName() {
    	return name;
    }
    
    /** 
     * Resets - as if no values have been added 
     */
    public void reset() {
        n = sum = absoluteSum = sumSquare = sumCube = 0;
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
    }

    /** 
     * Returns an identical RunningMetric 
     */
    public RunningMetric copy() {
        RunningMetric o = new RunningMetric(name);
        o.add(this);
        return o;
    }


    /** 
     * Contributes this new value to the mean and stdev 
     */
    public void add(double x) {
        n++;
        sum += x;
        absoluteSum += Math.abs(x);
        double square = x * x;
        sumSquare += square;
        sumCube += (x * square);
        if (x < min)
            min = x;
        if (x > max)
            max = x;
    }

    public void addw(double x, double w) {
        n += w;
        sum += x * w;
        absoluteSum += Math.abs(x) * w;
        double square = x * x * w;
        sumSquare += square;
        sumCube += (x * square);
        if (x < min)
            min = x;
        if (x > max)
            max = x;
    }

    /** 
     * Contributes this new values to the mean and stdev 
     */
    public void add(double[] x) {
        for (int i= 0; i< x.length; i++){
            add(x[i]);
        }
    }

    /** 
     * Contributes this new values to the mean and stdev 
     */
    public void add(int[] x) {
        for (int i= 0; i< x.length; i++){
            add(x[i]);
        }
    }

    /** 
     * Contributes the values from another RunningMetric 
     */
    public void add(RunningMetric rm) {
        n += rm.n;
        sum += rm.sum;
        absoluteSum += rm.absoluteSum;
        sumSquare += rm.sumSquare;
        sumCube += rm.sumCube;
        if (min > rm.min)
            min = rm.min;
        if (max < rm.max)
            max = rm.max;
    }

    /** 
     * The mean thusfar of all values that have been added 
     */
    public double getAverage() {
        if (n == 0)
            return 0;
        else
            return sum / n;
    }

    
    /** 
     * The mean thusfar of all values that have been added 
     */
    public double getMean() {
        return getAverage();
    }

    /** 
     * The absolute mean thusfar of all values that have been added 
     */
    public double getAbsoluteAverage() {
        if (n == 0)
            return 0;
        else
            return absoluteSum / n;
    }

    /** 
     * The absolute mean thusfar of all values that have been added 
     */
    public double getAbsoluteMean() {
        return getAbsoluteAverage();
    }
    
    /** 
     * The variance of all values that have been added (sample variance) 
     */
    public double getVariance() {
        if (n < 2)
            return 0;
        else {
            return (n * sumSquare - sum * sum) / (n * (n - 1)); 
        }
    }

    /** 
     * The standard deviation of all values that have been added 
     */
    public double getStd() {
        return Math.sqrt(getVariance());
    }

    /** 
     * The skewness of all values that have been added (sample skewness) 
     */
    public double getSkewness() {
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
    public double getMin() {
        return min;
    }

    /** 
     * The maximum of all values that have been added 
     */
    public double getMax() {
        return max;
    }

    /** 
     * The sum of all values that have been added 
     */
    public double getSum() {
        return sum;
    }

    /** 
     * The absolute sum of all values that have been added 
     */
    public double getAbsoluteSum() {
        return absoluteSum;
    }
    
    /** 
     * The number of values added 
     */
    public double getN() {
        return n;
    }

    /**
     * Returns an array containing, in this order: 
     * 		a[0] = n(); 
     *		a[1] = sum();
     * 		a[2] = min(); 
     * 		a[3] = max(); 
     * 		a[4] = mean(); 
     * 		a[5] = std();
     */
    public double[] asArray() {
        double[] a = new double[7];
        a[0] = getN();
        a[1] = getSum();
        a[2] = getMin();
        a[3] = getMax();
        a[4] = getMean();
        a[5] = getStd();
        a[6] = getSkewness();
        return a;
    }

    /** 
     * A string representation of: name, n(), sum(), min(), max(), mean(), std() 
     */
    @Override
	public String toString() {
        return name + ": n=" + n + ", sum=" + sum + ", min=" + min + ", max=" + max
                + ", avg=" + getAverage() + ", std=" + getStd() + ", skew=" + getSkewness();
    }

    /**
     * A convenience method - gets the means of an array of RunningMetrics
     */
    public static double[] getMean(RunningMetric[] metrics) {
        double[] m = new double[metrics.length];
        for (int i = 0; i < metrics.length; i++) {
            if (metrics[i] == null)
                m[i] = Double.NaN;
            else
                m[i] = metrics[i].getMean();
        }
        return m;
    }

    /**
     * A convenience method - gets the stds of an array of RunningMetrics
     */
    public static double[] getStd(RunningMetric[] metrics) {
        double[] std = new double[metrics.length];
        for (int i = 0; i < metrics.length; i++) {
            if (metrics[i] == null)
                std[i] = Double.NaN;
            else
                std[i] = metrics[i].getStd();
        }
        return std;
    }

    /**
     * A convenience method - gets the skewness of an array of RunningMetrics
     */
    public static double[] getSkewness(RunningMetric[] metrics) {
        double[] skewness = new double[metrics.length];
        for (int i = 0; i < metrics.length; i++) {
            if (metrics[i] == null)
                skewness[i] = Double.NaN;
            else
                skewness[i] = metrics[i].getSkewness();
        }
        return skewness;
    }

	public final double getSumSquare() {
		return sumSquare;
	}

}









