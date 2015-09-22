package ru.spbau.mit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Wowember on 22.09.2015.
 */

public class StringSetImpl implements StreamSerializable, StringSet{

    private class Vertex{

        private int countWithSamePrefix;
        private Vertex[] next;
        private boolean isTerminate;

        public Vertex(){
            countWithSamePrefix = 0;
            next = new Vertex[52];
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
            return 26 + (ch - 'a');
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

    public boolean contains(String element){
        Vertex now = root;
        for (int i = 0; i < element.length(); i++){
            int ind = getNumber(element.charAt(i));
            if (now.next[ind] == null)
                return false;
            now = now.next[ind];
        }
        return now.isTerminate;
     }

    public boolean remove(String element){
        if (!contains(element))
            return false;
        Vertex now = root;
        now.countWithSamePrefix--;
        for (int i = 0; i < element.length(); i++) {
            int ind = getNumber(element.charAt(i));
            now.next[ind].countWithSamePrefix--;
            Vertex tmp = now;
            now = now.next[ind];
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
        Vertex now = root;
        for (int i = 0; i < prefix.length(); i++){
            int ind = getNumber(prefix.charAt(i));
            if (now.next[ind] == null)
                return 0;
            now = now.next[ind];
        }
        return now.countWithSamePrefix;
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
        if (now.isTerminate) {
            out.write(intToByte(1));
        }
        else {
            out.write(intToByte(0));
        }
        for (int i = 0; i < 52; i++)
            if (now.next[i] == null) {
                out.write(intToByte(0));
            }
            else {
                out.write(intToByte(1));
            }
        for (int i = 0; i < 52; i++)
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

    private int getInt(int pos, byte[] ar){
        return ((ar[pos * 4 + 3] << 24) & 0xFF000000 | ((ar[pos * 4 + 2] << 16)& 0xFF0000) | ((ar[pos * 4 + 1] << 8) & 0xFF00) | (ar[pos * 4] & 0xFF));
    }

    private void recDeserialize(Vertex now, InputStream in) throws IOException {
        byte[] buffer = new byte[54*4];
        if (in.read(buffer) != 54*4)
            return;
        now.countWithSamePrefix = getInt(0, buffer);
        if (getInt(1, buffer) == 1)
            now.isTerminate = true;
        for (int i = 2; i < 54; i++){
            if (getInt(i, buffer) > 0)
                now.next[i - 2] = new Vertex();
        }
        for (int i = 0; i < 52; i++)
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
