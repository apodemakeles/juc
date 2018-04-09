package aqs;

/**
 * Created by Caozheng on 2018/4/9.
 */
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class SimpleCountDownLatchTest {

    @Test
    public void basis_test() throws InterruptedException{

        SimpleCountDownLatch latch = new SimpleCountDownLatch(5);
        latch.countDown();
        latch.countDown();
        latch.countDown();
        latch.countDown();
        latch.countDown();

        assertTrue(latch.await(1, TimeUnit.MILLISECONDS));
    }

    @Test
    public void concurrent_test() throws InterruptedException{
        SimpleCountDownLatch latch = new SimpleCountDownLatch(5);
        Runnable r = ()->{
            for(int i = 0;i < (1 << 30); i++) {

            }
            latch.countDown();
        };

        Utils.run(r);
        Utils.run(r);
        Utils.run(r);
        Utils.run(r);
        Utils.run(r);

        latch.await();

    }
}
