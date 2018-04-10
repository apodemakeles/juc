package aqs;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by Caozheng on 2018/4/10.
 */
public class SimpleRWLock implements ReadWriteLock {

    private final Sync sync = new Sync();
    private final RLock rLock;
    private final WLock wLock;

    public SimpleRWLock(){
        rLock = new RLock(sync);
        wLock = new WLock(sync);
    }

    private static class RLock implements Lock{
        private final Sync sync;

        public RLock(Sync sync){
            this.sync = sync;
        }

        public void lock(){
            sync.acquireShared(1);
        }

        public void lockInterruptibly() throws InterruptedException{
            sync.acquireSharedInterruptibly(1);
        }

        public boolean tryLock(){
            return sync.tryAcquireShared(1) >= 0;
        }

        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException{
            return sync.tryAcquireSharedNanos(1, unit.toNanos(time));
        }

        public void unlock(){
            sync.releaseShared(1);
        }

        public Condition newCondition(){
            return sync.newCondition();
        }

    }

    private static class WLock implements Lock{
        private final Sync sync;

        public WLock(Sync sync){
            this.sync = sync;
        }


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

    }


    public Lock readLock(){
        return rLock;
    }

    public Lock writeLock(){
        throw new NotImplementedException();
    }

    private static class Sync extends AbstractQueuedSynchronizer{
        static final int EXCLUSIVE = (1 << 16);

        @Override
        protected boolean tryAcquire(int arg){
            for(;;) {
                int c = getState();
                if (c == 0 && compareAndSetState(0, EXCLUSIVE)) {
                    return true;
                }else if (c != 0){
                    return false;
                }
            }
        }

        @Override
        protected boolean tryRelease(int arg){
            int c = getState();
            if (c != EXCLUSIVE){
                throw new IllegalMonitorStateException();
            }

            setState(0);

            return true;
        }

        @Override
        protected int tryAcquireShared(int arg){
            for(;;){
                int c = getState();
                if (c < EXCLUSIVE && compareAndSetState(c, c + 1)){
                    return 0;
                }else if (c == EXCLUSIVE){
                    return -1;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(int arg){
            for(;;){
                int c = getState();
                if (c == EXCLUSIVE){
                    throw new IllegalMonitorStateException();
                }
                if(compareAndSetState(c, c-1)) {
                    if (c == 1){
                        return true;
                    }

                    return false;
                }
            }
        }

        public ConditionObject newCondition() {
            return new ConditionObject();
        }

    }
}
