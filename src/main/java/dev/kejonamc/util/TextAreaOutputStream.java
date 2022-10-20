package dev.kejonamc.util;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TextAreaOutputStream extends OutputStream {
    private final byte[] oneByte;
    private Appender appender;

    public TextAreaOutputStream(JTextArea txtara) {
        this(txtara,1000);
    }

    public TextAreaOutputStream(JTextArea txtara, int maxlin) {
        if(maxlin<1) { throw new IllegalArgumentException("TextAreaOutputStream maximum lines must be positive (value="+maxlin+")"); }
        oneByte=new byte[1];
        appender=new Appender(txtara,maxlin);
    }

    public synchronized void clear() {
        if(appender!=null) { appender.clear(); }
    }

    public synchronized void close() {
        appender=null;
    }

    public synchronized void flush() {
    }

    public synchronized void write(int val) {
        oneByte[0]=(byte)val;
        write(oneByte,0,1);
    }

    public synchronized void write(@NotNull byte[] ba) {
        write(ba,0,ba.length);
    }

    public synchronized void write(@NotNull byte[] ba, int str, int len) {
        if(appender!=null) { appender.append(bytesToString(ba,str,len)); }
    }


    static private String bytesToString(byte[] ba, int str, int len) {
        return new String(ba,str,len, StandardCharsets.UTF_8);
    }

    static class Appender
            implements Runnable
    {
        private final JTextArea textArea;
        private final int maxLines;
        private final LinkedList<Integer> lengths;
        private final List<String> values;

        private int curLength;
        private boolean clear;
        private boolean queue;

        Appender(JTextArea txtara, int maxlin) {
            textArea =txtara;
            maxLines =maxlin;
            lengths  = new LinkedList<>();
            values   = new ArrayList<>();

            curLength=0;
            clear =false;
            queue =true;
        }

        synchronized void append(String val) {
            values.add(val);
            if(queue) { queue=false; EventQueue.invokeLater(this); }
        }

        synchronized void clear() {
            clear=true;
            curLength=0;
            lengths.clear();
            values.clear();
            if(queue) { queue=false; EventQueue.invokeLater(this); }
        }

        public synchronized void run() {
            if(clear) { textArea.setText(""); }
            for(String val: values) {
                curLength+=val.length();
                if(val.endsWith(EOL1) || val.endsWith(EOL2)) {
                    if(lengths.size()>=maxLines) { textArea.replaceRange("",0,lengths.removeFirst()); }
                    lengths.addLast(curLength);
                    curLength=0;
                }
                textArea.append(val);
            }
            values.clear();
            clear =false;
            queue =true;
        }

        static private final String         EOL1="\n";
        static private final String         EOL2=System.getProperty("line.separator",EOL1);
    }
}