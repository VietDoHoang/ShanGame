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
public class ItemVPlayer implements Serializable {
    public ItemVPlayer() {

    }


    private int id;
    private String N;
    private long AG;
    private Integer VIP;
    private int[] Arr;
    private Integer Av;
    private long FId;
    private short UserType;
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

    public int[] getArr() {
        return Arr;
    }

    public void setArr(int[] arr) {
        Arr = arr;
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

    public long getAG() {
        return AG;
    }
    
    public void setAG(long AG) {
        this.AG = AG;
    }

    public short getUserType() {
        return UserType;
    }

    public void setUserType(short userType) {
        UserType = userType;
    }

	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String str) {
		displayName = str;
	}
}

