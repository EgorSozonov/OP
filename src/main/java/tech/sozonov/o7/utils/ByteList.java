package tech.sozonov.o7.utils;
import java.nio.charset.StandardCharsets;

import lombok.val;


public class ByteList {
    private byte[] data;
    public int length;
    public int capacity;

    public ByteList() {
        capacity = 4;
        data = new byte[4];
        length = 0;
    }

    public ByteList(int initCapacity) {
        capacity = Math.max(initCapacity, 4);
        data = new byte[capacity];
        length = 0;
    }

    public void add(byte newByte) {
        if (length == capacity) {
            capacity *= 2;
            val newData = new byte[capacity];
            for (int i = 0; i < length; ++i) {
                newData[i] = data[i];
            }
            this.data = newData;
        }
        data[length] = newByte;
        ++length;
    }

    public void removeLast(int ind) {
    }

    public byte get(int ind) {
        return data[ind];
    }

    public void clear() {
        length = 0;
    }

    public byte last() {
        return data[length - 1];
    }

    public String toAsciiString() {
        if (length == 0) return "";
        val tmp = new byte[length];
        for (int i = 0; i < length; i++) {
            tmp[i] = data[i] < 128 ? data[i] : 0;
        }
        return new String(tmp, StandardCharsets.US_ASCII);
    }
}
