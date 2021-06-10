package org.ybuka.je.thread;

public class MultiThreadTask {

    public static class Task extends Thread {
        public String result;
        private long sleep;

        public Task(){
            this(-1);
        }
        public Task(long sleep){
            this.sleep = sleep;
        }
        public String doSomething() {
            if(sleep >0){
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return "I'm completed with error " + Thread.currentThread();
                }
            }
            return "I'm completed " + Thread.currentThread();
        }

        @Override
        public void run() {
            result = doSomething();
        }
    }

    public static void main(String[] args) {
        MultiThreadTask t = new MultiThreadTask();
        t.simpleTest();
    }

    public void simpleTest(){
        System.out.println("Start simple test");
        Task t1 = new Task();
        Task t2 = new Task();

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(t1.result);
        System.out.println(t2.result);

    }

}
