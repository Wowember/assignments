package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;

/**
 * Created by Wowember on 22.09.2015.
 */

public class StringSetImpl implements StreamSerializable, StringSet{

    private final static int ALPHABET_SIZE = 52;

    private static class Vertex{
        private int countWithSamePrefix = 0;
        private Vertex[] next = new Vertex[ALPHABET_SIZE];
        private boolean isTerminate = false;
    }

    private Vertex root = new Vertex();

    private int getNumber(char ch){
        if (Character.isUpperCase(ch)){
            return (ch - 'A');
        }
        else{
            return ALPHABET_SIZE / 2 + (ch - 'a');
        }
    }

    public boolean add(String element){
        if(contains(element)){
            return false;
        }
        Vertex now = root;
        now.countWithSamePrefix++;
        for (char c: element.toCharArray()){
            int ind = getNumber(c);
            if (now.next[ind] == null){
                now.next[ind] = new Vertex();
            }
            now = now.next[ind];
            now.countWithSamePrefix++;
        }
        now.isTerminate = true;
        return true;
    }

    private Vertex containsSubstring(String subStr){
        Vertex now = root;
        for (char c: subStr.toCharArray()){
            int ind = getNumber(c);
            if (now.next[ind] == null){
                return null;
            }
            now = now.next[ind];
        }
        return now;
    }

    public boolean contains(String element){
        Vertex now = containsSubstring(element);
        return now != null && now.isTerminate;
     }

    public boolean remove(String element){
        if (!contains(element)){
            return false;
        }
        Vertex now = root;
        now.countWithSamePrefix--;
        for (char c: element.toCharArray()){
            int ind = getNumber(c);
            if (now.next[ind].countWithSamePrefix == 1){
                now.next[ind] = null;
                return true;
            }
            now = now.next[ind];
            now.countWithSamePrefix--;
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

    private byte[] bitSetToByte(BitSet bits){
        byte[] bytes = new byte[ALPHABET_SIZE / 8 + 1];
        for (int i = 0; i < bits.length(); i++){
            if (bits.get(i)){
                bytes[i / 8] |= (byte) (1 << (i % 8));
            }
        }
        return bytes;
    }

    private void recSerialize(Vertex now, OutputStream out)throws IOException{
        BitSet bits = new BitSet(ALPHABET_SIZE + 1);
        for (int i = 0; i < ALPHABET_SIZE; i++){
            bits.set(i, now.next[i] != null);
        }
        bits.set(ALPHABET_SIZE, now.isTerminate);
        out.write(bitSetToByte(bits));
        for (int i = 0; i < ALPHABET_SIZE; i++){
            if (now.next[i] != null){
                recSerialize(now.next[i], out);
            }
        }
    }

    public void serialize(OutputStream out){
        try{
            recSerialize(root, out);
        }
        catch (IOException ex){
            throw new SerializationException();
        }
    }

    private void recDeserialize(Vertex now, InputStream in) throws IOException{
        byte[] buffer = new byte[ALPHABET_SIZE / 8 + 1];
        in.read(buffer);
        for (int i = 0; i < ALPHABET_SIZE; i++){
            if (((buffer[i / 8] >> (i % 8)) & 1) == 1){
                now.next[i] = new Vertex();
            }
        }
        now.isTerminate = ((buffer[ALPHABET_SIZE / 8] >> (ALPHABET_SIZE % 8)) & 1) == 1;
        now.countWithSamePrefix += now.isTerminate ? 1 : 0;
        for (int i = 0; i < ALPHABET_SIZE; i++){
            if (now.next[i] != null){
                recDeserialize(now.next[i], in);
                now.countWithSamePrefix += now.next[i].countWithSamePrefix;
            }
        }
    }

    public void deserialize(InputStream in){
        try{
            root = new Vertex();
            recDeserialize(root, in);
        }
        catch (IOException ex){
            throw new SerializationException();
        }
    }

}
