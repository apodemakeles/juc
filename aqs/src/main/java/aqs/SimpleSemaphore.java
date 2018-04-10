package aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by Caozheng on 2018/4/9.
 */
public class SimpleSemaphore {

    private final Sync sync;

    public SimpleSemaphore(int permit){
        sync = new Sync(permit);

    }

    public void acquire() throws InterruptedException{
        sync.acquireSharedInterruptibly(1);
    }

    public void acquireUninterruptibly(){
        sync.acquireShared(1);
    }

    public boolean tryAcquire(){
        return sync.tryAcquireShared(1) >= 0;
    }

    public boolean tryAcquire(long timeout, TimeUnit unit)
            throws InterruptedException{
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
    }

    public void release() {
        sync.releaseShared(1);
    }

    public final void reducePermits(int reductions){
        sync.reducePermits(reductions);
    }

    public final void producePermits(int productions){
        sync.releaseShared(productions);
    }

    public final int drainPermits() {
        return sync.drainPermits();
    }


    private class Sync extends AbstractQueuedSynchronizer {

        public Sync(int permit){
            setState(permit);
        }

        @Override
        protected int tryAcquireShared(int arg){
            int available = 0;
            int remaining = 0;

            do{
                available = getState();
                remaining = available - arg;
                if (remaining < 0){
                    return remaining;
                }
            }while(!compareAndSetState(available, remaining));

            return remaining;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            int available = 0;
            int remaining = 0;

            do{
                remaining = getState();
                available = remaining + arg;

            }while(!compareAndSetState(remaining, available));

            return true;

        }


        public final void reducePermits(int reductions) {
            while(true){
                int remaining = getState();
                if (compareAndSetState(remaining, remaining - reductions))
                    return;
            }
        }

        public final int drainPermits() {
            while(true){
                int remaining = getState();
                if (remaining == 0 || compareAndSetState(remaining, 0))
                    return remaining;
            }
        }
    }

}
