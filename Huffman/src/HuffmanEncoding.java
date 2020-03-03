import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
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
    public HuffmanEncoding(String str)
    {
        readText(str);
        putInQueue();
    }

    public static void writeBody(StringBuilder binChar, String outputFileName) {
        // write the bytes to the file
        while (binChar.length() > 8) {
            int readMax = 8 * (binChar.length() / 8);
            FileOutputHelper.writeBinStrToFile(binChar.substring(0, readMax), outputFileName);
            binChar.delete(0, readMax);
        }

        if (binChar.length() != 0) {
            for (int i = binChar.length(); i != 8; i++)
                binChar.append("0");
            FileOutputHelper.writeBinStrToFile(binChar.toString(), outputFileName);
        }
    }
    // Method that displays the header in file format.
    public static void writeHeader(String header, String outputFileName) {
        for (int i = 0; i < header.length(); i++) {
            String section = convertTo8bits(Integer.toBinaryString(header.charAt(i)));
            FileOutputHelper.writeBinStrToFile(section, outputFileName);
        }
    }
    public static String formatHeader(HashMap<String, String> codewords) {
        String header = "";

        for (Map.Entry<String, String> entry : codewords.entrySet())
            header += entry.getKey() + "," + entry.getValue() + "\n";
        return header + "\n";
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
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine() + "\n";
                original.append(line);
                countFreq(line,freq);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: File not found.");
            e.printStackTrace();
        }
        System.out.println("Original string is: \n" + original);
    }

    public void countFreq(String text,Map<Character,Integer> freq)
    {
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
        for (Map.Entry<Character,Integer> entry: freq.entrySet()){
            pq.add(new Node(entry.getKey(), entry.getValue()));
        }
        while (pq.size() != 1)
        {
            Node left = pq.poll();
            Node right = pq.poll();

            int sum = left.freq + right.freq;
            pq.add(new Node('\0',sum,left,right));
        }

        Node root = pq.peek();

        Map<Character,String> huffmanCode = new HashMap<>();
        encode(root,"",huffmanCode);
        System.out.println("Huffman Codes are :\n");
        for (Map.Entry<Character, String> entry : huffmanCode.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }

        // print encoded string
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

            System.out.println("CODE: "+ str);
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
            System.out.println("STR: "+ str);
            int val = Integer.parseInt(str);
            byte b = (byte) val;
            out.write(b);
            System.out.println(b);
        }
        out.flush();
//        BinaryIn in = new BinaryIn("encoded.txt");
//        while (!in.isEmpty()){
//            char c = in.readChar();
//            out.write(c);
//        }
//        out.flush();
//        writeBody(sb,"encoded.txt");

        System.out.println("\nEncoded string is :\n" + sb);

        // traverse the Huffman Tree again and this time
        // decode the encoded string
        int index = -1;
        File oFile = new File("decoded.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(oFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("\nDecoded string is: \n");
        while (index < sb.length() - 2) {
            index = decode(root, index, encoded,writer);
        }
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


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
