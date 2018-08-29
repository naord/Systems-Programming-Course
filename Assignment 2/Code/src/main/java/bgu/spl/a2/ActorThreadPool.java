package bgu.spl.a2;

import javafx.util.Pair;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
	private ConcurrentHashMap<String, PrivateState> actorsMap;
	private ConcurrentHashMap<String, ConcurrentLinkedQueue<Action>> qMap;
	private ConcurrentLinkedQueue<String> availableActorsQ;
	private Vector<String> busyActors;
	private Thread [] threadsArray;
	private AtomicBoolean isShutdown;
	private VersionMonitor shutDownLatch;
	private VersionMonitor eventLoopLatch;


	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */
	public ActorThreadPool(int nthreads) {
		this.shutDownLatch = new VersionMonitor();
		this.eventLoopLatch = new VersionMonitor();
		this.isShutdown = new AtomicBoolean(false);
		this.actorsMap = new ConcurrentHashMap<String, PrivateState>();
		this.qMap = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Action>>();
		this.availableActorsQ = new ConcurrentLinkedQueue<String>();
		this.busyActors = new Vector<>();
		this.threadsArray = new Thread [nthreads];
		for(int i=0; i<nthreads; i++){
			this.threadsArray[i] = new Thread(()->{
				while(!Thread.currentThread().isInterrupted() && !isShutdown.get()){
					threadAction();
				}
				shutDownLatch.inc();//here thread going out because he was interrupted
			});
		}
	}

	/**
	 * thread try to get an action if there are any available actors.
	 * in case there are no available actors the thread is waiting to get notify.
	 */
	private void threadAction() {
		Action action = null;
		String actorId = null;
		int tmp_eventLoopLatch = this.eventLoopLatch.getVersion();//save the version before loop
		Pair<String, Action> actorAndAction = getActorAndAction();
		if(actorAndAction != null) {
			action = actorAndAction.getValue();
			actorId = actorAndAction.getKey();
			action.handle(this, actorId, actorsMap.get(actorId));
			updateAvailableActorsQ(actorId);
			eventLoopLatch.inc();
		}
		else{//in case no available actors
			try{
				qMap.forEach((actor,actorQ)->{
				});
				eventLoopLatch.await(tmp_eventLoopLatch);//wait if all q are busy/empty and version did not changed while thread was in event loop.
			}
			catch(InterruptedException ex){
				Thread.currentThread().interrupt();//making sure interrupt flag is up.
			}
		}
	}

	/**
	 * thread is looking for actor and action from available actors queqe
	 * return pair if has available actors or null else
	 * @return pair- contains actorID and Action
	 * we need synchronized because we do want more than one thread accessing to actors queqe
	 * if more than one thread will access to this function we can pass the if conditions but the actor will not be available
	 */
	private synchronized Pair<String, Action> getActorAndAction(){
		Pair<String, Action> pair = null;
		String actorId = null;
		Action action = null;
		actorId = availableActorsQ.poll();
		if(actorId !=null){//if we have available actors
			action = qMap.get(actorId).poll();
			if(action != null){//check if q is empty
				pair = new Pair<>(actorId, action);
				busyActors.add(actorId);
			}
			else{
			}
		}
		return pair;
	}

	/**
	 * update the available actors queqe after handle
	 * we need synchronized because we do want more than one thread accessing to actors queqe
	 * if more than one thread will access to this function after the if contains we can try remove the same actor more than once
	 * also we can add actor who is not available to available quequ
	 * @param actorId
	 */
	private synchronized void updateAvailableActorsQ(String actorId){
		if(busyActors.contains(actorId)){//remove actor from busy Q
			busyActors.remove(actorId);
		}
		qMap.forEach((actor, actorQ)->{
			if(!busyActors.contains(actor) && !availableActorsQ.contains(actor) && !actorQ.isEmpty()){
				availableActorsQ.add(actor);
			}
		});
	}

	/**
	 * getter for actors
	 * @return actors
	 */
	public Map<String, PrivateState> getActors(){
		return this.actorsMap;
	}

	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId){
		return actorsMap.get(actorId);
	}


	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	/**synchronized is needed because if more than one thread is accessing to the actor map
	 * we can create more than one actor after the if
	 * also we want to make sure only one thread accessing to actor queqe
	 *
	 * */
	public synchronized void submit(Action<?> action, String actorId, PrivateState actorState) {
		Boolean isBusy = false;
		if (actorsMap.containsKey(actorId)) {//if actor exist
			if (busyActors.contains(actorId)) {//if actor exist and busy
				isBusy = true;
				qMap.get(actorId).add(action);
			}
			if (!isBusy) {//actor is not busy
				qMap.get(actorId).add(action);
				if (!(availableActorsQ.contains(actorId))) {//if actor exist and not available
					availableActorsQ.add(actorId);
				}
			}
		}
		else{//if actor not exist
			actorsMap.put(actorId, actorState);
			qMap.put(actorId, new ConcurrentLinkedQueue<>());
			qMap.get(actorId).add(action);
			availableActorsQ.add(actorId);//new actor has action and he is free
		}
		eventLoopLatch.inc();//notify
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		this.isShutdown.set(true);
		for (int i = 0; i<threadsArray.length; i++){
			threadsArray[i].interrupt();
		}
		shutDownLatch.await(threadsArray.length-1);//wait until there are no live threads
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for (int i=0; i<threadsArray.length; i++) {
				this.threadsArray[i].start();
		}

	}

}
