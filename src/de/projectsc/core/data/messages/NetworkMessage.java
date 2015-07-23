/*
 * Copyright (C) 2015 Project SC
 * 
 * All rights reserved
 */
package de.projectsc.core.data.messages;

/**
 * Message class for the network.
 * 
 * @author Josch Bosch
 */
public class NetworkMessage {

    private String msg;

    private Object[] data;

    public NetworkMessage() {

    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object... data) {
        this.data = data;
    }

}
