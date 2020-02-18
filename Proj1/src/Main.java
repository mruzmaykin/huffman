//public class Main implements Runnable {
//
//    public void run(){
//        System.out.println("hello world!");
//    }
//    public static void main(String[] args) {
//        new Thread(new Main()).start();
//	// write your code here
//    }
//}


/*
    Thread methods:
        Thread.Interrupted() - reads and clears
        Thread.isInterrupted() - reads only
 */
public class Main extends Thread {

    public void run(){
        System.out.println("hello thread");
    }
    public static void main(String[] args){
        new Thread(new Main()).start();
    }
}