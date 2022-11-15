/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.catte.logic.votranfer;

import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;

import net.catte.logic.vo.Card;

/**
 *
 * @author UserXP
 */
public class Packet implements Serializable{

	 public Packet(String evt,String data){
	        this.evt = evt;
	        this.data = data;
	 }

    public Packet(String evt, int pid, String userame) {
        this.evt = evt;
        this.pid = pid;
        this.userName = userName;
    }

    public Packet(String evt, int timeAction, int pid) {
        this.evt = evt;
        this.timeAction = timeAction;
        this.pid = pid;
    }
    public Packet(String evt, String username, String data) {
    	this.evt=evt;
    	this.userName=username;
    	this.data=data;
    }
    public Packet(String evt, String username, List<Card> card, int point) {
    	this.evt=evt;
    	this.userName=username;
    	this.card=card;
    	this.Point=point; 
    }
    public Packet(String evt, String username, Long ag, int round) {
    	this.evt=evt;
    	this.userName=username;
    	this.card=card;
    	
    }
    //packet betsmoney
    public Packet(String evt, String username, long betmoney) {
    	this.evt=evt;
    	this.userName=username;
    	this.betmoney=betmoney;
    }
    // packet listCard
    public Packet(String evt, List<Card> listcard) {
    	this.evt=evt;
    	this.card=listcard;
    }
    public Packet(String evt, String username, boolean take) {
    	this.evt=evt;
    	this.userName=username;
    	this.take=take;
    }

    private String evt;
    private String data;
    private int timeAction;
    private int pid;
    private String userName;
    private String typeWin;
    private List<Card> card;
    private int Point;
    private long AG;
    private long round;
    private long betmoney;
    private boolean take;
    
    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
    public String getTypeWin() {
        return typeWin;
    }

    public void setPid(String typewin) {
        this.typeWin=typewin;
    }

    /**
     * Get the value of data
     *
     * @return the value of data
     */
    public String getData() {
        return data;
    }

    /**
     * Set the value of data
     *
     * @param data new value of data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Get the value of evt
     *
     * @return the value of evt
     */
    public String getEvt() {
        return evt;
    }

    /**
     * Set the value of evt
     *
     * @param evt new value of evt
     */
    public void setEvt(String evt) {
        this.evt = evt;
    }
    
    public int getTimeAction() {
        return timeAction;
    }

    public void setTimeAction(int timeAction) {
        this.timeAction = timeAction;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
