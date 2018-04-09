package aqs;

/**
 * Created by Caozheng on 2018/4/9.
 */
public class Utils {

    public static Thread run(Runnable runnable){
        Thread t = new Thread(runnable);
        t.start();

        return t;

    }
}
