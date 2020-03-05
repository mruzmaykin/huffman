
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class BinaryOut {

    private BufferedOutputStream out;
    private int buffer;
    private int n;

    public BinaryOut(String filename) {
        try {
            OutputStream os = new FileOutputStream(filename);
            out = new BufferedOutputStream(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBit(boolean x) {
        // add bit to buffer
        buffer <<= 1;
        if (x) buffer |= 1;

        // if buffer is full (8 bits), write out as a single byte
        n++;
        if (n == 8) clearBuffer();
    }

    private void writeByte(int x) {
        assert x >= 0 && x < 256;

        // optimized if byte-aligned
        if (n == 0) {
            try {
                out.write(x);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // otherwise write one bit at a time
        for (int i = 0; i < 8; i++) {
            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    // write out any remaining bits in buffer to the binary output stream, padding with 0s
    private void clearBuffer() {
        if (n == 0) return;
        if (n > 0) buffer <<= (8 - n);
        try {
            out.write(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        n = 0;
        buffer = 0;
    }

    public void flush() {
        clearBuffer();
        try {
            out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void close() {
        flush();
        try {
            out.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void write(byte x) {
        writeByte(x & 0xff);
    }


    public void write(char x) {
        if (x < 0 || x >= 256) throw new IllegalArgumentException("Illegal 8-bit char = " + x);
        writeByte(x);
    }





}


