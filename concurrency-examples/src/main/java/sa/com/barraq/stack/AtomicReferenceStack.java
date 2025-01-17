package sa.com.barraq.stack;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceStack<T> {

    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicReference<StackNode<T>> top = new AtomicReference<>();

    public int size() {
        return count.get();
    }

    public void push(T newItem) {
        StackNode<T> oldTop;
        StackNode<T> newTop;
        do {
            oldTop = top.get();
            newTop = new StackNode<>(newItem);
            newTop.setNext(oldTop);
        } while (!top.compareAndSet(oldTop, newTop));

        count.incrementAndGet();
    }

    public T pop() {
        StackNode<T> oldTop;
        StackNode<T> newTop;

        do {
            oldTop = top.get();
            if (oldTop == null) return null;
            newTop = oldTop.getNext();
        } while (!top.compareAndSet(oldTop, newTop));

        count.decrementAndGet();
        return oldTop.getItem();
    }
}
