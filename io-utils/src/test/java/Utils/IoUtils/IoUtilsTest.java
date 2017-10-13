package Utils.IoUtils;

import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

public class IoUtilsTest {

    private static final int RBUF_SIZE = 127;
    private static final byte[] rBuf = new byte[RBUF_SIZE];
    private static final int LBUF_SIZE = 20 * 1024 * 1024;//20 MB
    private static final byte[] lBuf = new byte[LBUF_SIZE];
    public class IntContainer{
        IntContainer(int num){
            this.num=num;
        }
        public int num;
    }
    public class TestException extends Exception {
    }

    @BeforeClass
    public static void setUp(){
        for (byte i = 0; i < RBUF_SIZE; i++) {
            rBuf[i] = i;
        }
        for (int i = 0; i < LBUF_SIZE; i++) {
            lBuf[i] = (byte)(i % 255);
        }
    }
    @After
    public void cleanMemory() {
        System.gc();
    }
    private void assertArrays(byte[] bufE, int startIndexE, byte[] bufC, int startIndexC, int count){
        for (int i = 0;i < count; i++) {
            Assert.assertEquals(bufE[startIndexE + i], bufC[startIndexC + i]);
        }
    }
    @Test(expected = Exception.class)
    public void notContractTest() throws Exception {
        throw new Exception();
    }

    @Test
    public void testWrite() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int BUF_SIZE = 100;
        byte[] buf = new byte[BUF_SIZE];
        for (byte i=0; i < BUF_SIZE; i++){
            buf[i] = i;
        }
        IoUtils.write(output,buf,BUF_SIZE);
        Assert.assertArrayEquals(buf,output.toByteArray());
    }
    @Test
    public void testWritePart() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        final int BUF_SIZE = 100;
        byte[] buf = new byte[BUF_SIZE];
        byte[] testBuf = new byte[BUF_SIZE / 2];
        for (byte i=0; i < BUF_SIZE; i++){
            buf[i] = i;
            if (i < BUF_SIZE / 2){
                testBuf[i] = i;
            }
        }
        IoUtils.write(output, buf, BUF_SIZE / 2);
        Assert.assertArrayEquals(testBuf,output.toByteArray());
    }
    @Test
    public void testFileSize() throws IOException {
        final int BUF_SIZE = 100;
        String fileName = "testFileSize";
        byte[] buf = new byte[BUF_SIZE];
        FileOutputStream output = new FileOutputStream(fileName);
        IoUtils.write(output,buf,BUF_SIZE);
        output.close();
        long fileSize=IoUtils.fileSize(fileName);
        new File(fileName).delete();
        Assert.assertEquals(BUF_SIZE,fileSize);

    }
    @Test
    public void testRead() throws IOException {
        final int BUF_SIZE = 80;
        byte[] buf = new byte[BUF_SIZE + 1];
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        long readBytes = IoUtils.read(input, buf, BUF_SIZE);
        Assert.assertEquals(BUF_SIZE, readBytes);
        assertArrays(rBuf,0,buf,0,BUF_SIZE);
    }
    @Test
    public void testReadL() throws IOException {
        final int BUF_SIZE = LBUF_SIZE / 2;
        byte[] buf = new byte[BUF_SIZE + 1];
        ByteArrayInputStream input = new ByteArrayInputStream(lBuf);
        long readBytes = IoUtils.read(input, buf, BUF_SIZE);
        Assert.assertEquals(BUF_SIZE, readBytes);
        assertArrays(lBuf,0,buf,0,BUF_SIZE);
    }

    @Test
    public void testReadFullBuf() throws IOException {
        byte[] buf = new byte[RBUF_SIZE];
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        Assert.assertEquals(RBUF_SIZE, IoUtils.read(input, buf));
        Assert.assertArrayEquals(rBuf,buf);
    }
    @Test
    public void testReadFullBufL() throws IOException {
        byte[] buf = new byte[LBUF_SIZE];
        ByteArrayInputStream input = new ByteArrayInputStream(lBuf);
        Assert.assertEquals(LBUF_SIZE, IoUtils.read(input, buf));
        Assert.assertArrayEquals(lBuf,buf);
    }
    @Test
    public void testHandlerRead() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        IntContainer sumRead = new IntContainer(0);
        long totalRead = IoUtils.read(input, (buf, size) -> {
            assertArrays(rBuf, sumRead.num, buf, 0, size);
            sumRead.num += size;
        });
        Assert.assertEquals(RBUF_SIZE,sumRead.num);
        Assert.assertEquals(RBUF_SIZE,totalRead);
    }
    @Test
    public void testHandlerReadL() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(lBuf);
        IntContainer sumRead = new IntContainer(0);
        long totalRead = IoUtils.read(input, (buf, size) -> {
            assertArrays(lBuf, sumRead.num, buf, 0, size);
            sumRead.num += size;
        });
        Assert.assertEquals(LBUF_SIZE,sumRead.num);
        Assert.assertEquals(LBUF_SIZE,totalRead);
    }
    @Test
    public void testReadFull() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        byte[] buf = IoUtils.read(input);
        Assert.assertArrayEquals(rBuf,buf);
    }
    @Test
    public void testReadFullL() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(lBuf);
        byte[] buf = IoUtils.read(input);
        Assert.assertArrayEquals(lBuf,buf);
    }
    @Test
    public void testReadBufHandler() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        byte[] buf = new byte[RBUF_SIZE];
        IntContainer sumRead = new IntContainer(0);
        long totalRead = IoUtils.read(input, buf, (sBuf, size) -> {
            assertArrays(rBuf, sumRead.num, sBuf, 0, size);
            assertArrays(rBuf, sumRead.num, buf, sumRead.num, size);
            sumRead.num += size;
        });
        Assert.assertEquals(RBUF_SIZE, sumRead.num);
        Assert.assertEquals(RBUF_SIZE, totalRead);
    }
    @Test
    public void testReadBufHandlerL() throws IOException {
        final int BUF_SIZE = LBUF_SIZE / 2;
        ByteArrayInputStream input = new ByteArrayInputStream(lBuf);
        byte[] buf = new byte[BUF_SIZE];
        IntContainer sumRead = new IntContainer(0);
        long totalRead = IoUtils.read(input, buf, (sBuf, size) -> {
            assertArrays(lBuf, sumRead.num, sBuf, 0, size);
            assertArrays(lBuf, sumRead.num, buf, 0, size);
            sumRead.num += size;

        });
        Assert.assertEquals(sumRead.num, totalRead);
    }
    @Test(expected = TestException.class)
    public void testReadBufHandlerEx() throws IOException,  TestException {
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        byte[] buf = new byte[RBUF_SIZE];
        long totalRead = IoUtils.readEx(input,buf, (sBuf, size) -> {
            throw new TestException();
        });
        Assert.fail();
    }
    @Test
    public void testReadMaxBytes() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        byte[] buf = new byte[RBUF_SIZE * 2];
        IntContainer sumRead = new IntContainer(0);
        long totalRead = IoUtils.read(input, RBUF_SIZE, buf, (sBuf, size) -> {
            assertArrays(rBuf, sumRead.num, sBuf, 0,size);
            assertArrays(rBuf, sumRead.num, buf, sumRead.num, size);
            sumRead.num += size;
        });
        Assert.assertEquals(RBUF_SIZE, sumRead.num);
        Assert.assertEquals(RBUF_SIZE, totalRead);
    }
    @Test
    public void testReadMaxBytesL() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(lBuf);
        byte[] buf = new byte[LBUF_SIZE * 2];
        IntContainer sumRead = new IntContainer(0);
        long totalRead = IoUtils.read(input, LBUF_SIZE, buf, (sBuf, size) -> {
            assertArrays(lBuf, sumRead.num, sBuf, 0,size);
            assertArrays(lBuf, sumRead.num, buf, sumRead.num, size);
            sumRead.num += size;
        });
        Assert.assertEquals(LBUF_SIZE, sumRead.num);
        Assert.assertEquals(LBUF_SIZE, totalRead);
    }
    @Test(expected = TestException.class)
    public void testReadMaxBytesEx() throws IOException, TestException {
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        byte[] buf = new byte[RBUF_SIZE * 2];
        long totalRead = IoUtils.readEx(input, RBUF_SIZE, buf, (sBuf, size) -> {
            throw new TestException();
        });
        Assert.fail();
    }
    @Test
    public void testCopy() throws IOException {
        ByteArrayInputStream input = new ByteArrayInputStream(rBuf);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        long totalCopy = IoUtils.copy(input, output);
        Assert.assertEquals(RBUF_SIZE, totalCopy);
        Assert.assertArrayEquals(output.toByteArray(), rBuf);
    }
}
