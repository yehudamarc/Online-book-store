package bgu.spl.mics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
    private static final Logger logger = LogManager.getLogger(Future.class.getSimpleName());

    // Guarantees that the latest value of a result is always visible to other threads running on other CPUs
	private volatile T result;
	private boolean isResolved;
	
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
        this.isResolved = false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public synchronized T get() {
        while (!isDone()) {
            try {
                this.wait();
                return this.result;
            } catch (InterruptedException e) {
                return null;
            }
        }

        return this.result;
    }
	
	/**
     * Resolves the result of this Future object.
     */
	public synchronized void resolve (T result) {
		this.result = result;
		this.isResolved = true;
		this.notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return this.isResolved;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public synchronized T get(long timeout, TimeUnit unit) {
        if (!isDone()) {
            try {
                unit.timedWait(this, timeout);
                if (isDone()) {
                    return this.result;
                }

                logger.warn("Future: " + this + " did not got resolve on the given time: " + timeout + " " + unit);
                return null;
            } catch (InterruptedException e) {
                return null;
            }
        }

        return this.result;
	}
}
