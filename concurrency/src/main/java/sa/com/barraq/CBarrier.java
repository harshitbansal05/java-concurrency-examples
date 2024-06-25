package sa.com.barraq;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CBarrier {

    public static void main(String[] args) {
        final CyclicBarrier cb = new CyclicBarrier(3, () -> {
            System.out.println("All parties have arrived at the barrier, lets continue execution.");
        });

        Thread t1 = new Thread(new Task(cb), "Thread 1");
        Thread t2 = new Thread(new Task(cb), "Thread 2");
        Thread t3 = new Thread(new Task(cb), "Thread 3");

        t1.start();
        t2.start();
        t3.start();
    }

    record Task(CyclicBarrier cb) implements Runnable {

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " is waiting on barrier");
                cb.await();
                System.out.println(Thread.currentThread().getName() + " has crossed the barrier");
            } catch (InterruptedException | BrokenBarrierException ex) {
                ex.printStackTrace();
            }
        }
    }
}
