import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

class Bits {
    final BitSet bitset;
    final int nbits;

    Bits(BitSet bitset, int nbits) {
        this.bitset = bitset;
        this.nbits = nbits;
    }

    int[] values() {
        int[] v = new int[nbits];
        for (int i = bitset.nextSetBit(0); i >= 0; i = bitset.nextSetBit(i + 1)) {
            v[i] = 1;
        }
        return v;
    }
}
public class HuffmanEncoding {
    private BitOutputStream output;
    private long start1;
    private float sec1;
    private float sec2;
    public HuffmanEncoding(String str)
    {
        System.out.println("Parallel tree creation and encoding");
        start1 = System.currentTimeMillis();
        readText(str);
        putInQueue();
    }

    public static String convertTo8bits(String binary) {
        String zeros = "";

        for (int i = 0; i != (8 - binary.length()); i++)
            zeros += "0";
        return zeros + binary;
    }



    class Node
    {
        char ch;
        int freq;
        Node left = null, right = null;

        Node(char ch, int freq)
        {
            this.ch = ch;
            this.freq = freq;
        }

        public Node(char ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }
    };

    PriorityQueue<Node> pq;
    Map<Character,Integer> freq;
    StringBuilder original;
    StringBuilder encoded;

    public void readText(String path)
    {
        original = new StringBuilder();
        freq = new HashMap<>();
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            for (int i = 0; i < 7; i++){
                myReader.nextLine();
            }
            myReader.close();
            //read file into stream, try-with-resources
            try (Stream<String> stream = Files.lines(Paths.get(path))) {
                stream.forEach( e -> countFreq(e,freq));
            } catch (IOException e) {
                e.printStackTrace();
            }
//
//            while (myReader.hasNextLine()) {
//
//                String line = myReader.nextLine() + "\n";
//
//                countFreq(line,freq);
//            }
//            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            e.printStackTrace();
        }
        //System.out.println("Original string is: \n" + original);
    }

    public void countFreq(String text,Map<Character,Integer> freq)
    {
        original.append(text);
        for (int i = 0 ; i < text.length(); i++) {
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
        sec1 = (end1 - start1) / 1000F;
        System.out.println("Create the tree time: " + sec1 + " seconds");

        long start2 = System.currentTimeMillis();

        Node root = pq.peek();

        Map<Character,String> huffmanCode = new HashMap<>();
        encode(root,"",huffmanCode);


        StringBuilder sb = new StringBuilder();
        Stream<Character> characterStream = original.chars().mapToObj(i -> original.charAt(i));
        characterStream.forEach(e -> sb.append(huffmanCode.get(e)));
        BinaryOut out = new BinaryOut("coded.txt");
        String code = sb.toString();
        List<String> strBytes = splitEqually(code,8);
        Stream<String> stringStream = strBytes.stream();
        stringStream.forEach(str -> out.write((byte)Integer.parseInt(str)));
        out.flush();
        long end2 = System.currentTimeMillis();
        //finding the time difference and converting it into seconds
        sec2 = (end2 - start2) / 1000F;
        System.out.println("Encode the file using the tree time: " + sec2 + " seconds");
    }
    public static int decode(Node root, int index, StringBuilder sb,FileWriter writer)
    {
        if (root == null)
            return index;

        // found a leaf node
        if (root.left == null && root.right == null)
        {
            try {
                writer.append(root.ch);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.print(root.ch);
            return index;
        }

        index++;

        if (sb.charAt(index) == '0')
            index = decode(root.left, index, sb,writer);
        else
            index = decode(root.right, index, sb,writer);

        return index;
    }

    public void encode(Node root, String str, Map<Character,String> code)
    {
        if(root == null)
            return;
        if (root.left == null && root.right == null)
            code.put(root.ch,str);
        encode(root.left,str+"0",code);
        encode(root.right,str+"1",code);

    }
}
