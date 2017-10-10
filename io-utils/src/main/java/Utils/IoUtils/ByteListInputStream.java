package Utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ByteListInputStream extends InputStream {

    private final List<Byte> buf;
    private int pos;

    public ByteListInputStream() {
        this(new ArrayList<>());
    }

    public ByteListInputStream(List<Byte> buf) {
        this.buf = buf;
    }

    @Override
    public int read() throws IOException {
        if (pos < buf.size()) {
            return buf.get(pos++);
        }
        return IoUtils.EOF;
    }

    @Override
    public int available() throws IOException {
        return buf.size() - pos;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        len = Math.min(available(), len) + pos;
        while (pos < len) {
            b[off++] = buf.get(pos++);
        }
        return len - pos;
    }

    @Override
    public long skip(long n) throws IOException {
        if (n < 0) {
            return 0;
        }
        n = Math.min(available(), n);
        pos += n;
        return n;
    }

    protected List<Byte> getBuf() {
        return buf;
    }

    protected int getPos() {
        return pos;
    }

    protected void setPos(int pos) {
        this.pos = pos;
    }
}
