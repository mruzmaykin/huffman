import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public final static int M = 3;
    public final static int K = 2;
    public final static int N = 3;
    public final static int[] threads = {1,1,2,2,4,4,8,8};
    public final static int [][] A = {{7,8}, {4,3}, {8,6}}; //Initializes A
    public final static int [][] B = {{5,4,7}, {3,4,7}};    //Initializes B
    public static int [][] C ;

    public static void main(String[] args) {
        for (int threadNum : threads) {
            long start = System.currentTimeMillis();
            List<Future> futures = new ArrayList<Future>();
            C = new int [M][N];
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadNum);
            for (int i = 0; i < M; i++) {
                for (int j = 0; j < N; j++) {
                    Matmul mult = new Matmul(i, j, A, B, C);
                    futures.add(executor.submit(mult));
                }
            }
            for (Future f: futures)
                {
                    try {
                        f.get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            executor.shutdown();
            long time = System.currentTimeMillis() - start;
            System.out.println("Time for "+threadNum+" threads: " + time/1000.0);
        }
        printC();
    }
    public static void printC(){
        //Outputs the Values of Matrix C
        System.out.println("Elements of Matrix C:");
        for (int i = 0; i<M; i++){
            System.out.print("[" + C[0][0]);
            for (int j=1; j<N; j++){
                System.out.print(" " + C[i][j] );
            }
            System.out.println("]");
        }
    }

}

class Matmul implements Runnable{

    private int row;
    private int col;
    private int [][] A;
    private int [][] B;
    private int [][] C;

    public Matmul(int row, int col, final int[][] A,
                        final int[][] B, int[][] C) {
        this.row = row;
        this.col = col;
        this.A = A;
        this.B = B;
        this.C = C;
    }

    @Override
    public void run() {
        C[row][col] = (A[row][0] * B[0][col])+ (A[row][1]*B[1][col]) ;
    }
}

