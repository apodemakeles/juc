package aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by Caozheng on 2018/4/9.
 */
public class SimpleCountDownLatch {

    private Sync sync;

    public SimpleCountDownLatch(int count){
        sync = new Sync(count);
    }

    public void await() throws InterruptedException{
        sync.await();
    }

    public boolean await(long timeout, TimeUnit unit)
            throws InterruptedException {

        return sync.await(timeout, unit);
    }

    public void countDown() {
        sync.releaseShared(1);
    }

    private class Sync extends AbstractQueuedSynchronizer{
        private final int count;

        public Sync(int count){
            this.count = count;
            setState(0);
        }

        public void await() throws InterruptedException{
            acquireSharedInterruptibly(this.count);
        }

        public boolean await(long timeout, TimeUnit unit)
                throws InterruptedException {
            return tryAcquireSharedNanos(this.count, unit.toNanos(timeout));
        }

        @Override
        protected int tryAcquireShared(int arg){
            do{
                int remain = getState();
                if (arg <= remain) {
                    if (compareAndSetState(remain, remain - arg)){
                        return remain - arg;
                    }
                    continue;
                }

                return remain - arg;

            }while(true);
        }

        @Override
        protected boolean tryReleaseShared(int arg){

            int remain = 0;

            do{
                remain = getState();
                if (remain + arg > this.count){
                    throw new IllegalMonitorStateException();
                }

            }while(!compareAndSetState(remain, remain + arg));

            return remain + arg == this.count;
        }

    }


}
