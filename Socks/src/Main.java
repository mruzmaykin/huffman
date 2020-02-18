import java.util.*;
import java.util.concurrent.*;

enum Color{
    Red, Blue, Green, Orange
}

public class Main {

    static final int MAX_SOCKS = 10;
    static final int MAX_COLORS = 4;

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        LinkedBlockingQueue<Sock> socksQueue = new LinkedBlockingQueue<>();
        Random rand = new Random();
        for (int i = 0; i < MAX_COLORS; i++){
            Color sockColor = Color.values()[i];
            SockProducer sockProd= new SockProducer(sockColor,rand.nextInt(MAX_SOCKS),socksQueue);
            executor.submit(sockProd);
        }
        SockMatcher sockMatcher = new SockMatcher(socksQueue);
        executor.submit(sockMatcher);
    }
}

class SockProducer implements Runnable {
    private Color color;
    private int socksNum;
    private boolean running;
    LinkedBlockingQueue<Sock> sockQueue;
    SockProducer(Color socksColor, int numSocks, LinkedBlockingQueue<Sock> queue){
        socksNum = numSocks;
        color = socksColor;
        sockQueue = queue;
        running = true;
        System.out.println(color.toString() + " Sock Producer: initiated creation of " + socksNum + " " + color.toString() + " socks.");
    }
    public boolean isRunning() {
        return running;
    }
    @Override
    public void run(){
        for (int i = 1; i <= socksNum; i++){
            Sock newSock = new Sock(color);
            sockQueue.add(newSock);
            System.out.println(newSock.sockColor.toString() + " Sock: Produced " + i + " of " + socksNum + " " + color + " socks.");
        }
        running = false;
        System.out.println(color.toString() + " Sock Producer: finished creation of " + socksNum + " socks.");
    }
}


class Sock implements  Comparable<Sock>{
    Color sockColor;

    Sock(Color color){
        sockColor = color;
    }
    public Color getSockColor(){
        return sockColor;
    }

    @Override
    public int compareTo(Sock sock) {
        return this.sockColor.compareTo(sock.sockColor);
    }
}

class SockMatcher implements Runnable{
    int[] socksNum;
    private LinkedBlockingQueue<Sock> sockQueue;
    private HashMap<Color,Sock> socks;
    SockMatcher(LinkedBlockingQueue<Sock> queue){
        this.sockQueue = queue;
        this.socks = new HashMap<>();
        this.socksNum = new int[Main.MAX_COLORS];
        for (int i = 0; i < Main.MAX_COLORS; i++){
            socksNum[i] = 0;
        }
    }

    @   Override
    public void run(){
        while(true) {
            if (sockQueue.isEmpty()) {

                try {
                    System.out.println("Queue: ");
                    for (Color x : socks.keySet()) {
                        System.out.print(socks.get(x).sockColor + " ");
                    }
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Sock s = sockQueue.take();
                    Color color = s.sockColor;
                    if (socks.containsKey(color)) {
                        socks.remove(color);
                        socksNum[color.ordinal()] += 2;
                        System.out.println("Sock Matcher: removed " + socksNum[color.ordinal()] + " " + color + " socks.");
                    } else {
                        socks.put(color, s);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
