package com.plectix.simulator.interfaces;

/**
 * Interface of random generator.
 * @author avokhmin
 *
 */
public interface IRandom {

	/**
	 * Returns the next pseudorandom, uniformly distributed
     * {@code double} value between {@code 0.0} and
     * {@code 1.0} from this random number generator's sequence.
	 * @return the next pseudorandom, uniformly distributed {@code double}
     *         value between {@code 0.0} and {@code 1.0} from this
     *         random number generator's sequence
	 */
	public double getDouble();

	/**
	 * Returns a pseudorandom, uniformly distributed {@code int} value
     * between 0 (inclusive) and the specified value (exclusive), drawn from
     * this random number generator's sequence.
	 * @param limit the bound on the random number to be returned.  Must be
     *	      positive.
	 * @return the next pseudorandom, uniformly distributed {@code int}
     *         value between {@code 0} (inclusive) and {@code limit} (exclusive)
     *         from this random number generator's sequence
	 */
	public int getInteger(int limit);
	
    /**
     * Sets the seed of this random number generator using a single 
     * <code>long</code> seed. The general contract of <tt>setSeed</tt> 
     * is that it alters the state of this random number generator
     * object so as to be in exactly the same state as if it had just 
     * been created with the argument <tt>seed</tt> as a seed. 
     * 
     * @param   seed   the initial seed.
     */
	public void setSeed(long seed);

}
