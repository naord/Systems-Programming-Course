package bgu.spl.a2;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T>
 *            the result type, <boolean> resolved - initialized ;
 */
public class Promise<T>{
	private AtomicBoolean resolved = new AtomicBoolean(false);
	private T result;
	private ConcurrentLinkedQueue<callback> callbackList = new ConcurrentLinkedQueue<callback>();

	/**
	 *
	 * @return the resolved value if such exists (i.e., if this object has been
	 *         {@link #resolve(java.lang.Object)}ed 
	 * @throws IllegalStateException
	 *             in the case where this method is called and this object is
	 *             not yet resolved
	 */

	public T get() {
	    if(resolved.get()){
	        return result;
	    }
		throw new IllegalStateException("this object is not yet resolved");
	}

	/**
	 *
	 * @return true if this object has been resolved - i.e., if the method
	 *         {@link #resolve(java.lang.Object)} has been called on this object
	 *         before.
	 */
	public boolean isResolved() {
		return resolved.get();
	}

	/**
	 * resolve this promise object - from now on, any call to the method
	 * {@link #get()} should return the given value
	 *
	 * Any callbacks that were registered to be notified when this object is
	 * resolved via the {@link #subscribe(callback)} method should
	 * be executed before this method returns
	 *
     * @throws IllegalStateException
     * 			in the case where this object is already resolved
	 * @param value
	 *            - the value to resolve this promise object with
	 *
	 * we need to synchronize this function because if more than one thread will access
	 * the promise can be resolved more than once
	 * also we can use call function to null
	 */
	public synchronized void resolve(T value){
	    if(resolved.get()){
	        throw new IllegalStateException("Promise is already resolved");
        }
        boolean tmp;
		this.result = value;
		do{
			tmp = resolved.get();
		}while(!(resolved.compareAndSet(tmp, true)));
		while(!callbackList.isEmpty()) {
			callbackList.poll().call();//Retrieves and removes the head (first element) of this list, than call the callback
		}
	}

	/**
	 * add a callback to be called when this object is resolved. If while
	 * calling this method the object is already resolved - the callback should
	 * be called immediately
	 *
	 * Note that in any case, the given callback should never get called more
	 * than once, in addition, in order to avoid memory leaks - once the
	 * callback got called, this object should not hold its reference any
	 * longer.
	 *
	 * @param callback
	 *            the callback to be called when the promise object is resolved
	 *
	 *  we need to synchronize this function because we dont want to add callback
	 *  while other thread is resolving the promise
	 *  if such think is happening the call back might not be called
	 *  (if the callback added after while loop in resolve)
	 */
	public synchronized void subscribe(callback callback) {
		if (resolved.get()){
			callback.call();
		}
		else{
			if (!callbackList.contains(callback)){
				callbackList.add(callback);
			}
		}
	}
}
