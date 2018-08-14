package bgu.spl.a2.sim;
import bgu.spl.a2.Promise;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 *
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private Promise<Boolean> promise;
	private AtomicBoolean isFree;
	private Computer computer;

	/**
	 * Construct and initialize the SuspendingMutex.
	 * @param computer
	 */
	public SuspendingMutex(Computer computer){
		this.computer = computer;
		this.isFree = new AtomicBoolean(true);
		this.promise = new Promise<>();
	}
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 *
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down(){
		Promise<Computer> promise = new Promise<>();//creating promise for each request
		acquireComputer(promise);//try to acquire the computer
		return promise;
	}
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 */
	public void up(){
		this.isFree.set(true);
		this.promise.resolve(true);//resolve this promis<Boolean> thus making sure to get callback if there is waiting promise.
	}

	/**
	 * This method try to acquire computer,
	 * if it is free resolve the promise<computer> else subscribe promise to this.
	 * @param promise - promise for computer
	 */
	private void acquireComputer(Promise<Computer> promise){
		if(this.isFree.compareAndSet(true, false)){
			promise.resolve(this.computer);
		}
		else{
			this.promise.subscribe(()->{
				acquireComputer(promise);
			});
		}
	}
}
