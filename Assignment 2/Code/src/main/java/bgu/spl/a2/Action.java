package bgu.spl.a2;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {
    protected Promise<R> promise = new Promise<>();
    protected AtomicReference<String> actionName= new AtomicReference<String>("");
    protected ActorThreadPool pool;
    protected String actorId;
    protected PrivateState actorState;
    protected AtomicBoolean isStarted = new AtomicBoolean(false);
    protected callback callback;

    /**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    protected abstract void start();

    /**
     * start/continue handling the action
     *
     * this method should be called in order to start this action
     * or continue its execution in the case where it has been already started.
     *
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     * @param pool - The pool the action works on.
     * @param actorId - The type of actor this action related to.
     * @param actorState - The actor's private state.
     */
    /*package*/ final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
        if(isStarted.compareAndSet(false, true)){//action need to start
            this.pool = pool;
            this.actorId = actorId;
            this.actorState = actorState;
            this.start();
        }
        else{//action need to continue
            isStarted.set(false);
            this.callback.call();
        }
    }

    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     *
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions - A collection of actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, final callback callback) {
        this.callback = callback;
        boolean tmp;
        do {
            tmp = isStarted.get();
        } while(!(isStarted.compareAndSet(tmp, true)));
        AtomicInteger leftActions = new AtomicInteger(actions.size());
        for(Action<?> action : actions){
            action.getResult().subscribe(()->{//subscribe callback that once action is resolved reduce counter of left actions once all the actions resolved subscribing the callback we need to do then
                int currNumber = leftActions.get();
                while(!leftActions.compareAndSet(currNumber,currNumber-1 )){
                    currNumber = leftActions.get();
                }
                if (currNumber == 1){
                   sendMessage(this, this.actorId, this.actorState);//add the action back to the pool to be done;
                }
            });
        }
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        this.promise.resolve(result);
        this.actorState.addRecord(getActionName());
    }

    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
        return this.promise;
    }

    /**
     * send an action to an other actor
     *
     * @param action
     * 				the action
     * @param actorId
     * 				actor's id
     * @param actorState
     * 				actor's private state (actor's information)
     *
     * @return promise that will hold the result of the sent action
     */
    public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState){
        this.pool.submit(action, actorId, actorState);
        return action.getResult();
    }

    /**
     * Setter to action's name
     * @param actionName
     */
    public void setActionName(String actionName){
        String tmp;
        do{
            tmp = this.actionName.get();
        }while (!(this.actionName.compareAndSet(tmp, actionName)));
    }

    /**
     * Getter to the action's name.
     * @return action's name
     */
    public String getActionName(){
        return this.actionName.get();
    }
}
