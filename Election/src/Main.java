import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {



    public static final String[] names =  {"Williams Scholl",
            "Alene Gohr",
            "Kip Harsch",
            "Leslie Arteaga",
            "Bryant Jarvie",
            "Rafaela Quaranta",
            "Clyde Koser",
            "Kristopher Bumgarner",
            "Lael Sires",
            "Austin Losh",
            "Irena Worcester",
            "Dante Render",
            "Florance Egner",
            "Darryl Melius",
            "Elodia Sobus",
            "Beata Nobles",
            "Antony Ballengee",
            "Shaunna Wagers",
            "Hazel Dupler",
            "Meri Farthing",
            "Randall Bhatti",
            "Golden Corbett",
            "Martin Salvador",
            "Yuette Funk",
            "Adriana Tetterton",
            "Myong Paiz",
            "Ferdinand Stickel",
            "Beaulah Walsh",
            "Billie Sama",
            "Norene Caughey",
            "Petronila Searight",
            "Parker Partridge",
            "Shiela Doak",
            "Danyell Yelton",
            "Marshall Marchi",
            "Tina Hertlein",
            "Tomasa Bazaldua",
            "Jeana Apicella",
            "Ada Smoak",
            "Noma Mccusker",
            "Donnette Rothermel",
            "Rosario Brasch",
            "Nena Middlebrooks",
            "Despina Haas",
            "Myesha Koziel",
            "Francesca Behar",
            "Simona Niswander",
            "Juliane Gabbert",
            "Trish Bewley",
            "Alysha Stuber"
    };

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++){
            ElectedOfficial e = new ElectedOfficial();
            executor.submit(e);
        }
	// write your code here
    }

}

class ElectedOfficial implements Runnable{
    private final String name;
    private final int rank;
    ElectedOfficial() {
        Random rand = new Random();
        name = Main.names[rand.nextInt(Main.names.length)];
        rank = rand.nextInt();

        System.out.println("My name is " + name);
        System.out.println("Rank: " + rank);
    }

    @   Override
    public void run(){
    }
    public void takeLeadership() throws Exception{
        final int waitTime = (int)(5*Math.random() + 1);
        

    }
}

class Rank