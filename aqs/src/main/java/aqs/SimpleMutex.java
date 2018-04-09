package aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Caozheng on 2018/4/5.
 */
public class SimpleMutex implements Lock {
    private final Sync sync = new Sync();

    public void lock(){
        sync.acquire(1);
    }

    public void lockInterruptibly() throws InterruptedException{
        sync.acquireInterruptibly(1);
    }

    public boolean tryLock(){
        return sync.tryAcquire(1);
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException{
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    public void unlock(){
        sync.release(1);
    }

    public Condition newCondition(){
        return sync.newCondition();
    }

    private static class Sync extends AbstractQueuedSynchronizer{

        @Override
        protected boolean tryAcquire(int arg){
            if (Thread.currentThread() == getExclusiveOwnerThread()){
                setState(getState() + 1);
                return true;
            }

            if (compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }

            return false;
        }

        @Override
        protected boolean tryRelease(int arg){
            int c = getState() - 1;
            if (Thread.currentThread() == getExclusiveOwnerThread()){
                if (c == 0){
                    setExclusiveOwnerThread(null);
                    setState(0);
                    return true;
                }

                setState(c);
                return false;
            }

            throw new IllegalMonitorStateException();

        }

        private void increaseCounter(){
            int i = getState();
            setState(i + 1);
        }

        private void decreaseCounter(){
            int i = getState();
            setState(i - 1);
        }

        final ConditionObject newCondition() {
            return new ConditionObject();
        }

        protected final boolean isHeldExclusively() {
            return getExclusiveOwnerThread() == Thread.currentThread();
        }
    }
}
