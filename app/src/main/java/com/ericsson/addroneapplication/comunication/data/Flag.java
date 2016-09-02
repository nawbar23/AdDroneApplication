package com.ericsson.addroneapplication.comunication.data;

/**
 * Created by nbar on 2016-09-02.
 */

public class Flag {

    final private int size;
    private int flag;


    public Flag(int size){
        this.size = size;
        this.flag = 0;
    }

    public Flag(int size, int initial){
        this.size = size;
        this.flag = initial;
    }

    void setFlagState(int id, boolean state) throws Exception{
        if (id > size) {
            throw new Exception("Id to set flag state out of range!");
        }
        if (state) {
            flag |= 1 << id;
        } else {
            flag &= ~(1 << id);
        }
    }

    boolean getFlagState(int id) throws Exception{
        if (id > size) {
            throw new Exception("Id to get flag state out of range!");
        }
        return (flag & (1 << id)) == 1;
    }

    public int getSize() {
        return size;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
