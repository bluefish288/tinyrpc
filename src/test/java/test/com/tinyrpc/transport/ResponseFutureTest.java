package test.com.tinyrpc.transport;


import com.tinyrpc.remoting.exchange.ResponseFuture;
import org.junit.Test;

public class ResponseFutureTest {

    @Test
    public void test() throws InterruptedException {


        testGetValue1();
        testGetValue2();
        testHasException1();
        testHasException2();


    }

    private void testGetValue1() throws InterruptedException {
        ResponseFuture future = new ResponseFuture();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(future.getValue());
            }
        });

        t.start();


        Thread.sleep(4000);
        future.setValue("test");
    }

    private void testGetValue2() throws InterruptedException {
        ResponseFuture future = new ResponseFuture();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(future.getValue());
            }
        });

        t.start();


        Thread.sleep(5001);
        future.setValue("test");
    }

    private void testHasException1() throws InterruptedException {
        ResponseFuture future = new ResponseFuture();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(future.isHasException());
            }
        });

        t.start();


        Thread.sleep(4999);
        future.setValue("test");
    }

    private void testHasException2() throws InterruptedException {
        ResponseFuture future = new ResponseFuture();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(future.isHasException());
            }
        });

        t.start();


        Thread.sleep(5001);
        future.setValue("test");
    }
}
