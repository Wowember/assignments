package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Wowember on 22.09.2015.
 */

public class StringSetImpl implements StreamSerializable, StringSet{

    private final static int ALPHABET_SIZE = 52;

    private static class Vertex{

        private int countWithSamePrefix;
        private Vertex[] next;
        private boolean isTerminate;

        private Vertex(){
            countWithSamePrefix = 0;
            next = new Vertex[ALPHABET_SIZE];
            isTerminate = false;
        }
    }

    private Vertex root;

    public StringSetImpl(){
        root = new Vertex();
    }

    private int getNumber(char ch){
        if (Character.isUpperCase(ch))
            return (ch - 'A');
        else
            return ALPHABET_SIZE / 2 + (ch - 'a');
    }

    public boolean add(String element){
        if(contains(element))
            return false;
        Vertex now = root;
        now.countWithSamePrefix++;
        for (int i = 0; i < element.length(); i++){
            int ind = getNumber(element.charAt(i));
            if (now.next[ind] == null)
                now.next[ind] = new Vertex();
            now = now.next[ind];
            now.countWithSamePrefix++;
        }
        now.isTerminate = true;
        return true;
    }

    private Vertex containsSubstring(String subStr){
        Vertex now = root;
        for (int i = 0; i < subStr.length(); i++){
            int ind = getNumber(subStr.charAt(i));
            if (now.next[ind] == null)
                return null;
            now = now.next[ind];
        }
        return now;
    }

    public boolean contains(String element){
        Vertex now = containsSubstring(element);
        return now != null && now.isTerminate;
     }

    public boolean remove(String element){
        if (!contains(element))
            return false;
        Vertex now = root;
        now.countWithSamePrefix--;
        for (int i = 0; i < element.length(); i++) {
            int ind = getNumber(element.charAt(i));
            Vertex tmp = now;
            now = now.next[ind];
            now.countWithSamePrefix--;
            if (now.countWithSamePrefix == 0)
                tmp.next[ind] = null;
        }
        now.isTerminate = false;
        return true;
    }

    public int size(){
        return root.countWithSamePrefix;
    }

    public int howManyStartsWithPrefix(String prefix){
        Vertex now = containsSubstring(prefix);
        return (now == null ? 0 : now.countWithSamePrefix);
    }

    private byte[] intToByte(int a){
        byte[] tmp = new byte[4];
        tmp[0] = (byte) a;
        tmp[1] = (byte) (a>>8);
        tmp[2] = (byte) (a>>16);
        tmp[3] = (byte) (a>>24);
        return tmp;
    }

    private void recSerialize(Vertex now, OutputStream out)throws IOException {
        out.write(intToByte(now.countWithSamePrefix));
        out.write(intToByte(now.isTerminate ? 1 : 0));
        for (int i = 0; i < ALPHABET_SIZE; i++)
            out.write(intToByte(now.next[i] == null ? 0 : 1));
        for (int i = 0; i < ALPHABET_SIZE; i++)
            if (now.next[i] != null)
                recSerialize(now.next[i], out);
    }

    public void serialize(OutputStream out) {
        try {
            recSerialize(root, out);
        }
        catch (IOException ex){
            throw new SerializationException();
        }
    }

    private int getInt(byte[] ar){
        return ((ar[3] << 24) & 0xFF000000 | ((ar[2] << 16)& 0xFF0000) | ((ar[1] << 8) & 0xFF00) | (ar[0] & 0xFF));
    }

    private void recDeserialize(Vertex now, InputStream in) throws IOException {
        byte[] buffer = new byte[4];
        in.read(buffer);
        now.countWithSamePrefix = getInt(buffer);
        in.read(buffer);
        now.isTerminate = (getInt(buffer) == 1);
        for (int i = 0; i < ALPHABET_SIZE; i++){
            in.read(buffer);
            if (getInt(buffer) == 1)
                now.next[i] = new Vertex();
        }
        for (int i = 0; i < ALPHABET_SIZE; i++)
            if (now.next[i] != null)
                recDeserialize(now.next[i], in);
    }

    public void deserialize(InputStream in){
        try {
            root = new Vertex();
            recDeserialize(root, in);
        }
        catch (IOException ex){
            throw new SerializationException();
        }
    }

}
