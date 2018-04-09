package aqs;

/**
 * Created by Caozheng on 2018/4/9.
 */

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleMutexTest {

    private SimpleMutex m = new SimpleMutex();

    @Test
    public void basis_test(){

        m.lock();
        m.unlock();

        m.lock();
        m.lock();
        m.unlock();
        m.unlock();

        assertTrue(m.tryLock());
        assertTrue(m.tryLock());
        m.unlock();
        m.unlock();

    }

    @Test
    public void interrupt_test() throws InterruptedException{
        m.lock();

        Thread t = Utils.run(()->{
            try {
                m.lockInterruptibly();
            }
            catch (InterruptedException e){

            }
        });

        t.interrupt();

        t.join();
    }

    @Test
    public void concurrent_test() throws InterruptedException{
        System.out.println(1 << 30);
        m.lock();
        Runnable r = ()->{
            m.lock();
            for(int i = 0;i < (1 << 30); i++) {

            }
            m.unlock();
        };

        Thread t1 = Utils.run(r);
        Thread t2 = Utils.run(r);
        Thread t3 = Utils.run(r);
        Thread t4 = Utils.run(r);
        Thread t5 = Utils.run(r);

        m.unlock();

        t1.join();
        t2.join();
        t3.join();
        t4.join();
        t5.join();
    }

}
