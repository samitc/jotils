package Utils.IoUtils;

import Utils.function.BiConsumerException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;

public class IoUtils {
    public static final int NO_MAX_BYTE_TO_READ = -1;
    static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 2;

    public static long fileSize(final String path) {
        return new File(path).length();
    }

    private static int read0(InputStream input, byte[] data, int size) throws IOException {
        return input.read(data, 0, size);
    }

    private static void write0(OutputStream output, byte[] data, int size) throws IOException {
        output.write(data, 0, size);
    }

    public static long read(InputStream input, byte[] data, int size) throws IOException {
        int curDataPos = 0;
        return read(input, size, new byte[DEFAULT_BUFFER_SIZE], (bytes, integer) -> System.arraycopy(bytes, 0, data, curDataPos, integer));
    }

    public static void write(OutputStream output, byte[] data, int size) throws IOException {
        write0(output, data, size);
    }

    public static long read(InputStream input, byte[] data, BiConsumer<byte[], Integer> readHandler) throws IOException {
        final int dataSize = data.length;
        long count = 0;
        int curCount;
        while (EOF != (curCount = read0(input, data, dataSize))) {
            readHandler.accept(data, curCount);
            count += curCount;
        }
        return count;
    }

    public static <T extends Exception> long readEx(InputStream input, byte[] data, BiConsumerException<byte[], Integer, T> readHandler) throws IOException, T {
        final int dataSize = data.length;
        long count = 0;
        int curCount;
        while (EOF != (curCount = read0(input, data, dataSize))) {
            readHandler.accept(data, curCount);
            count += curCount;
        }
        return count;
    }

    public static long read(InputStream input, long maxBytesToRead, byte[] data, BiConsumer<byte[], Integer> readHandler) throws IOException {
        if (maxBytesToRead == NO_MAX_BYTE_TO_READ) {
            return read(input, data, readHandler);
        } else if (maxBytesToRead == 0) {
            return 0;
        }
        final int dataSize = data.length;
        int curMaxBytesToRead = (int) Math.min(dataSize, maxBytesToRead);
        long count = 0;
        int curCount;
        do {
            curCount = read0(input, data, curMaxBytesToRead);
            if (curCount == EOF) {
                return count;
            }
            readHandler.accept(data, curCount);
            count += curCount;
            curMaxBytesToRead = (int) Math.min(dataSize, (maxBytesToRead - count));
        } while (curMaxBytesToRead > 0);
        return count;
    }

    public static <T extends Exception> long readEx(InputStream input, long maxBytesToRead, byte[] data, BiConsumerException<byte[], Integer, T> readHandler) throws IOException, T {
        if (maxBytesToRead == NO_MAX_BYTE_TO_READ) {
            return readEx(input, data, readHandler);
        } else if (maxBytesToRead == 0) {
            return 0;
        }
        final int dataSize = data.length;
        int curMaxBytesToRead = (int) Math.min(dataSize, maxBytesToRead);
        long count = 0;
        int curCount;
        do {
            curCount = read0(input, data, curMaxBytesToRead);
            if (curCount == EOF) {
                return count;
            }
            readHandler.accept(data, curCount);
            count += curCount;
            curMaxBytesToRead = (int) Math.min(dataSize, (maxBytesToRead - count));
        } while (curMaxBytesToRead > 0);
        return count;
    }

    public static long copy(InputStream input, OutputStream output, long maxBytesToCopy, byte[] data) throws IOException {
        return readEx(input, maxBytesToCopy, data, (bytes, integer) -> write0(output, bytes, integer));
    }

    public static long copy(InputStream input, OutputStream output, long maxBytesToCopy) throws IOException {
        return copy(input, output, maxBytesToCopy, new byte[DEFAULT_BUFFER_SIZE]);
    }

    public static long copy(InputStream input, OutputStream output) throws IOException {
        return copy(input, output, NO_MAX_BYTE_TO_READ, new byte[DEFAULT_BUFFER_SIZE]);
    }
}
