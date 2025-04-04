package pcd.ass01.utils.sync;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleBarrier {

    private final int parties;
    private int count = 0;
    private int released = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public SimpleBarrier(int parties) {
        this.parties = parties;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            count++;
            if (count == parties) {
                // Last thread has arrived: reset and release all
                released = parties;
                count = 0;
                condition.signalAll();
            } else {
                while (released == 0) {
                    condition.await();
                }
            }

            released--;
        } finally {
            lock.unlock();
        }
    }
}
