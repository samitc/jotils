package Utils.IoUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ByteListOutputStream extends OutputStream {
    private final List<Byte> buf;

    public ByteListOutputStream() {
        this(new ArrayList<>());
    }

    public ByteListOutputStream(List<Byte> buf) {
        this.buf = buf;
    }

    @Override
    public void write(int b) throws IOException {
        buf.add((byte) b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        while (off < len) {
            write(b[off++]);
        }
    }

    protected List<Byte> getBuf() {
        return buf;
    }

}
