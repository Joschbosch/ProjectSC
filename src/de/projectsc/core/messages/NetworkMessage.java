/*
 * Project SC - 2015
 * 
 * 
 */
package de.projectsc.core.messages;

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
