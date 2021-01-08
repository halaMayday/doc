import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    public void run() {
        Fork fork1 = new Fork(1);
        Fork fork2 = new Fork(2);
        Fork fork3 = new Fork(3);

        Philosopher socrates = new Philosopher("Socrates", fork1, fork2);
        Philosopher plato = new Philosopher("Plato", fork2, fork3);
        Philosopher aristotle = new Philosopher("Aristotle", fork3, fork1);

        List<Philosopher> philosophers = new ArrayList<>();
        philosophers.add(socrates);
        philosophers.add(plato);
        philosophers.add(aristotle);

        List<Thread> threads = new ArrayList<>();
        for (Philosopher philosopher : philosophers) {
            Thread thread = new Thread(philosopher);
            thread.start();
            threads.add(thread);
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    private static class Fork {
        private int num;
        private final ReentrantLock lock = new ReentrantLock();

        public Fork(int num) {
            this.num = num;
        }

        public void acquire() {
            lock.lock();
        }

        public void release() {
            lock.unlock();
        }

        @Override
        public String toString() {
            return "fork-" + num;
        }
    }

    private static class Philosopher implements Runnable {
        private final String name;
        private final Fork leftFork;
        private final Fork rightFork;

        public Philosopher(String name, Fork leftFork, Fork rightFork) {
            this.name = name;
            this.leftFork = leftFork;
            this.rightFork = rightFork;
        }

        public String getName() {
            return name;
        }

        private void think() {
            System.out.printf("%s is thinking ...%n", name);
        }

        private void eat() {
            try {
                leftFork.acquire();
                System.out.printf("%s acquired %s on left%n", name, leftFork);
                rightFork.acquire();
                System.out.printf("%s acquired %s on right%n", name, rightFork);
                System.out.printf("%s is eating ...%n", name);
            } finally {
                rightFork.release();
                System.out.printf("%s released %s on right%n", name, rightFork);
                leftFork.release();
                System.out.printf("%s released %s on left%n", name, leftFork);
            }
        }

        @Override
        public void run() {
            while (true) {
                think();
                eat();
            }
        }
    }

    public static void main(String[] args) {
        new DiningPhilosophers().run();
    }
}
