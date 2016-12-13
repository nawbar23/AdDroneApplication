package com.addrone.multicopter.data;

/**
 * Created by nbar on 2016-09-02.
 */
public class Flags {

    final private int size;
    private int flags;


    public Flags(int size){
        this.size = size;
        this.flags = 0;
    }

    public Flags(int size, int initial){
        this.size = size;
        this.flags = initial;
    }

    void setFlagsState(int id, boolean state) throws Exception{
        if (id > size) {
            throw new Exception("Id to set flag state out of range!");
        }
        if (state) {
            flags |= 1 << id;
        } else {
            flags &= ~(1 << id);
        }
    }

    boolean getFlagState(int id) throws Exception{
        if (id > size) {
            throw new Exception("Id to get flag state out of range!");
        }
        return (flags & (1 << id)) == 1;
    }

    public int getSize() {
        return size;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flag) {
        this.flags = flag;
    }
}
