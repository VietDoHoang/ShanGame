/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.catte.logic.votranfer;

import java.io.Serializable;

/**
 *
 * @author UserXP
 */
public class ItemPlayer implements Serializable {

    public ItemPlayer() {

    }
    private int id;
    private String N;
    private long AG;
    private Integer LQ;
    private Integer VIP;
    private Integer Av;
    private long FId;
    private int UserType;
    private long TotalAG;
    private int timeToStart;
    private float LQ0;
    private String displayName;

    private int keyObjectInGame;
	
	public void setKeyObjectInGame(int keyObject) {
		this.keyObjectInGame = keyObject;
	}
	
	public int getKeyObjectIngame() {
		return keyObjectInGame;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getFId() {
        return FId;
    }

    public void setFId(long fId) {
        FId = fId;
    }

    public Integer getAv() {
        return Av;
    }

    public void setAv(Integer av) {
        Av = av;
    }

    public String getN() {
        return N;
    }

    public void setN(String n) {
        N = n;
    }

    public Integer getVIP() {
        return VIP;
    }

    public void setVIP(Integer vIP) {
        VIP = vIP;
    }

    public Integer getLQ() {
        return LQ;
    }

    public void setLQ(Integer lQ) {
        LQ = lQ;
    }

    /**
     * Get the value of Mark100
     *
     * @return the value of Mark100
     */
    public long getAG() {
        return AG;
    }

    /**
     * Set the value of Mark100
     *
     * @param AG
     */
    public void setAG(long AG) {
        this.AG = AG;
    }

    public int getUserType() {
        return UserType;
    }

    public void setUserType(int userType) {
        UserType = userType;
    }

    public long getTotalAG() {
        return TotalAG;
    }

    public void setTotalAG(long totalAG) {
        TotalAG = totalAG;
    }

    public void setTimeToStart(int time) {
        this.timeToStart = time;
    }

    public int getTimeToStart() {
        return timeToStart;
    }
    
    public void setLQ0(float LQ0) {
    	this.LQ0 = LQ0;
    }
    
    public float getLQ0() {
    	return LQ0;
    }

	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String str) {
		displayName = str;
	}
}
