package tech.sozonov.o7.utils;
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

    public byte get(int ind) throws Exception {
        if (ind < 0 || ind >= length) {
            throw new Exception("Out of bounds exception, length = " + length + ", ind = " + ind);
        }
        return data[ind];
    }

    public void clear() {
        length = 0;
    }

    public byte last() {
        return data[length - 1];
    }
}
