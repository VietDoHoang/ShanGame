/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.catte.logic.votranfer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.catte.logic.vo.GameStatus;

/**
 *
 * @author UserXP
 */
public class TableVInfo implements Serializable {

    public TableVInfo() {
        ArrP = new ArrayList<>();
    }

    private long pot;

    private Integer M;
    private List<ItemVPlayer> ArrP;
    private int Id;
    private Integer AG;
    private Integer S;
    private String CN; //Nguoi choi hien tai
    private Integer CT; //So thoi gian con lai
    private Integer CA; //Current Action 0 - Boc, 1 - Danh, 2- Ha bai
    private long TotalAG;
    private String dealerName;
    private int rate;
    private int score;
    private GameStatus gameStatus;

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public long getPot() {
        return pot;
    }

    public void setPot(long pot) {
        this.pot = pot;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getTotalAG() {
        return TotalAG;
    }

    public void setTotalAG(long TotalAG) {
        this.TotalAG = TotalAG;
    }

    public Integer getCT() {
        return CT;
    }

    public void setCT(Integer cT) {
        CT = cT;
    }

    public Integer getCA() {
        return CA;
    }

    public void setCA(Integer cA) {
        CA = cA;
    }

    public String getCN() {
        return CN;
    }

    public void setCN(String cN) {
        CN = cN;
    }

    public Integer getS() {
        return S;
    }

    public void setS(Integer s) {
        S = s;
    }

    public Integer getAG() {
        return AG;
    }

    public void setAG(Integer aG) {
        AG = aG;
    }

    public Integer getM() {
        return M;
    }

    public void setM(Integer m) {
        M = m;
    }

    public List<ItemVPlayer> getArrP() {
        return ArrP;
    }

    public void setArrP(List<ItemVPlayer> arrP) {
        ArrP = arrP;
    }

    /**
     * Get the value of Id
     *
     * @return the value of Id
     */
    public int getId() {
        return Id;
    }

    /**
     * Set the value of Id
     *
     * @param Id new value of Id
     */
    public void setId(int Id) {
        this.Id = Id;
    }

}
