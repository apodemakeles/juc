package aqs;

/**
 * Created by Caozheng on 2018/4/10.
 */

import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleSemaphoreTest {

    @Test
    public void basis_test() throws InterruptedException {
        SimpleSemaphore semaphore = new SimpleSemaphore(5);
        for (int i = 0; i < 5; i++) {
            semaphore.acquire();
        }

        for (int i = 0; i < 5; i++) {
            semaphore.release();
        }

        semaphore.drainPermits(); // state == 0
        assertFalse(semaphore.tryAcquire());
        semaphore.producePermits(1); // state == 1
        assertTrue(semaphore.tryAcquire()); // state == 0

        semaphore.reducePermits(1); // state == -1
        semaphore.release(); // state == 0
        assertFalse(semaphore.tryAcquire());
    }

    @Test
    public void concurrent_test() throws InterruptedException{
        SimpleSemaphore semaphore = new SimpleSemaphore(5);

        for (int i=0;i<5;i++) {
            semaphore.acquire();
            Utils.run(()->{
                for (int j = 0; j < (1 << 30); j++) {

                }
                semaphore.release();
            });
        }

        semaphore.acquire();
    }

}
