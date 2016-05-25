/*
 * Copyright (C) 2016 
 */

package de.projectsc.core.data.utils.fbxloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.InflaterInputStream;

public class FBXReader {

    public static final int BLOCK_SENTINEL_LENGTH = 13;

    public static final byte[] BLOCK_SENTINEL_DATA = new byte[BLOCK_SENTINEL_LENGTH];

    /**
     * Majic string at start: "Kaydara FBX Binary\x20\x20\x00\x1a\x00"
     */
    public static final byte[] HEAD_MAGIC = new byte[] { 0x4b, 0x61, 0x79, 0x64, 0x61, 0x72, 0x61, 0x20, 0x46, 0x42, 0x58, 0x20, 0x42,
        0x69, 0x6e, 0x61, 0x72, 0x79, 0x20, 0x20, 0x00, 0x1a, 0x00 };

    public static FBXFile readFBX(InputStream stream) throws IOException {
        FBXFile fbxFile = new FBXFile();
        // Read file to byte buffer so we can know current position in file
        ByteBuffer byteBuffer = readToByteBuffer(stream);
        try {
            stream.close();
        } catch (IOException e) {
        }
        // Check majic header
        byte[] majic = getBytes(byteBuffer, HEAD_MAGIC.length);
        if (!Arrays.equals(HEAD_MAGIC, majic))
            throw new IOException("Either ASCII FBX or corrupt file. "
                + "Only binary FBX files are supported");

        // Read version
        fbxFile.setVersion(getUInt(byteBuffer));
        // Read root elements
        while (true) {
            FBXElement e = readFBXElement(byteBuffer);
            if (e == null)
                break;
            fbxFile.addRootElement(e);
        }
        return fbxFile;
    }

    private static FBXElement readFBXElement(ByteBuffer byteBuffer) throws IOException {
        long endOffset = getUInt(byteBuffer);
        if (endOffset == 0)
            return null;
        long propCount = getUInt(byteBuffer);
        getUInt(byteBuffer); // Properties length unused

        FBXElement element = new FBXElement((int) propCount);
        element.setId(new String(getBytes(byteBuffer, getUByte(byteBuffer))));

        for (int i = 0; i < propCount; ++i) {
            char dataType = readDataType(byteBuffer);
            element.getProperties().add(readData(byteBuffer, dataType));
            element.getPropertiesTypes()[i] = dataType;
        }
        if (byteBuffer.position() < endOffset) {
            while (byteBuffer.position() < (endOffset - BLOCK_SENTINEL_LENGTH)) {
                element.getChildren().add(readFBXElement(byteBuffer));
            }

            if (!Arrays.equals(BLOCK_SENTINEL_DATA, getBytes(byteBuffer, BLOCK_SENTINEL_LENGTH)))
                throw new IOException("Failed to read block sentinel, expected 13 zero bytes");
        }
        if (byteBuffer.position() != endOffset)
            throw new IOException("Data length not equal to expected");
        return element;
    }

    private static Object readData(ByteBuffer byteBuffer, char dataType) throws IOException {
        switch (dataType) {
        case 'Y':
            return byteBuffer.getShort();
        case 'C':
            return byteBuffer.get() == 1;
        case 'I':
            return byteBuffer.getInt();
        case 'F':
            return byteBuffer.getFloat();
        case 'D':
            return byteBuffer.getDouble();
        case 'L':
            return byteBuffer.getLong();
        case 'R':
            return getBytes(byteBuffer, (int) getUInt(byteBuffer));
        case 'S':
            return new String(getBytes(byteBuffer, (int) getUInt(byteBuffer)));
        case 'f':
            return readArray(byteBuffer, 'f', 4);
        case 'i':
            return readArray(byteBuffer, 'i', 4);
        case 'd':
            return readArray(byteBuffer, 'd', 8);
        case 'l':
            return readArray(byteBuffer, 'l', 8);
        case 'b':
            return readArray(byteBuffer, 'b', 1);
        case 'c':
            return readArray(byteBuffer, 'c', 1);
        }
        throw new IOException("Unknown data type: " + dataType);
    }

    private static Object readArray(ByteBuffer byteBuffer, char type, int bytes) throws IOException {
        int count = (int) getUInt(byteBuffer);
        int encoding = (int) getUInt(byteBuffer);
        int length = (int) getUInt(byteBuffer);

        byte[] data = getBytes(byteBuffer, length);
        if (encoding == 1)
            data = inflate(data);
        if (data.length != count * bytes)
            throw new IOException("Wrong data lenght. Expected: " + count * bytes + ", got: " + data.length);
        ByteBuffer dis = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        switch (type) {
        case 'f':
            float[] arr = new float[count];
            for (int i = 0; i < count; ++i)
                arr[i] = dis.getFloat();
            return arr;
        case 'i':
            int[] arr2 = new int[count];
            for (int i = 0; i < count; ++i)
                arr2[i] = dis.getInt();
            return arr2;
        case 'd':
            double[] arr3 = new double[count];
            for (int i = 0; i < count; ++i)
                arr3[i] = dis.getDouble();
            return arr3;
        case 'l':
            long[] arr4 = new long[count];
            for (int i = 0; i < count; ++i)
                arr4[i] = dis.getLong();
            return arr4;
        case 'b':
            boolean[] arr5 = new boolean[count];
            for (int i = 0; i < count; ++i)
                arr5[i] = dis.get() == 1;
            return arr5;
        case 'c':
            int[] arr6 = new int[count];
            for (int i = 0; i < count; ++i)
                arr6[i] = dis.get() & 0xFF;
            return arr6;
        }
        throw new IOException("Unknown array data type: " + type);
    }

    private static byte[] inflate(byte[] input) throws IOException {
        InflaterInputStream gzis = new InflaterInputStream(new ByteArrayInputStream(input));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (gzis.available() > 0) {
            int l = gzis.read(buffer);
            if (l > 0)
                out.write(buffer, 0, l);
        }
        return out.toByteArray();
    }

    private static char readDataType(ByteBuffer byteBuffer) {
        return (char) byteBuffer.get();
    }

    private static long getUInt(ByteBuffer byteBuffer) {
        return byteBuffer.getInt() & 0x00000000ffffffffL;
    }

    private static int getUByte(ByteBuffer byteBuffer) {
        return byteBuffer.get() & 0xFF;
    }

    private static byte[] getBytes(ByteBuffer byteBuffer, int size) {
        byte[] b = new byte[size];
        byteBuffer.get(b);
        return b;
    }

    private static ByteBuffer readToByteBuffer(InputStream input) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
        byte[] tmp = new byte[2048];
        while (true) {
            int r = input.read(tmp);
            if (r == -1)
                break;
            out.write(tmp, 0, r);
        }
        return ByteBuffer.wrap(out.toByteArray()).order(ByteOrder.LITTLE_ENDIAN);
    }
}
