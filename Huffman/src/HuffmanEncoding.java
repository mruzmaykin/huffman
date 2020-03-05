import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;
public class HuffmanEncoding {
    private long start1;
    public HuffmanEncoding(String str)
    {
        System.out.println("Parallel tree creation");
        start1 = System.currentTimeMillis();
        readText(str);
        putInQueue();
    }


    public void putInQueue()
    {
        pq = new PriorityQueue<>((l,r) -> l.freq - r.freq);
        Stream<Map.Entry<Character, Integer>> entriesStream = freq.entrySet().stream();
//        for (Map.Entry<Character,Integer> entry: freq.entrySet()){
//            pq.add(new Node(entry.getKey(), entry.getValue()));
//        };
        entriesStream.forEach(e -> pq.add(new Node(e.getKey(), e.getValue())));
        while (pq.size() != 1)
        {
            Node left = pq.poll();
            Node right = pq.poll();

            int sum = left.freq + right.freq;
            pq.add(new Node('\0',sum,left,right));
        }

        long end1 = System.currentTimeMillis();
        //finding the time difference and converting it into seconds
        float sec1 = (end1 - start1) / 1000F;
        System.out.println("Create the tree time: " + sec1 + " seconds");

        long start2 = System.currentTimeMillis();

        Node root = pq.peek();

        Map<Character,String> huffmanCode = new HashMap<>();
        encode(root,"",huffmanCode);
        encoded = new StringBuilder();
        File eFile = new File("encoded.txt");
        FileWriter ewriter = null;
        try {
            ewriter = new FileWriter(eFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < original.length(); i++) {
            String str = huffmanCode.get(original.charAt(i));
            sb.append(str);
            try {
                ewriter.append(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ewriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BinaryOut out = new BinaryOut("coded.txt");

        String code = sb.toString();
        List<String> strBytes = splitEqually(code,8);

        for (int i = 0; i < strBytes.size(); i++){

            String str = strBytes.get(i);
            int val = Integer.parseInt(str);
            byte b = (byte) val;
            out.write(b);
        }
        out.close();
        long end2 = System.currentTimeMillis();
        //finding the time difference and converting it into seconds
        float sec2 = (end2 - start2) / 1000F;
        System.out.println("Encode the file using the tree time: " + sec2 + " seconds");
    }

    PriorityQueue<Node> pq;
    Map<Character, Integer> freq;
    StringBuilder original;
    StringBuilder encoded;

    public void readText(String path) {
        original = new StringBuilder();
        freq = new HashMap<>();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            for (int i = 0; i < 7; i++) {
                myReader.nextLine();
            }
            myReader.close();
            //read file into stream, try-with-resources
            try (Stream<String> stream = Files.lines(Paths.get(path))) {
                stream.forEach(e -> countFreq(e, freq));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            e.printStackTrace();
        }
    }

    public void countFreq(String text, Map<Character, Integer> freq) {
        original.append(text);
        for (int i = 0; i < text.length(); i++) {
            if (!freq.containsKey(text.charAt(i))) {
                freq.put(text.charAt(i), 0);
            }
            freq.put(text.charAt(i), freq.get(text.charAt(i)) + 1);
        }
    }

    public static List<String> splitEqually(String text, int size) {
        // Give the list the right capacity to start with. You could use an array
        // instead if you wanted.
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);

        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }

    class Node {
        char ch;
        int freq;
        Node left = null, right = null;

        Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }

        public Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
    }

    public void encode(Node root, String str, Map<Character, String> code) {
        if (root == null)
            return;
        if (root.left == null && root.right == null)
            code.put(root.ch, str);
        encode(root.left, str + "0", code);
        encode(root.right, str + "1", code);

    }
}
