package net.catte.logic.votranfer;

import java.util.ArrayList;
import java.util.List;

public class TableInfo {

    public TableInfo(){
        ArrP = new ArrayList<ItemPlayer>();
    }
    private Integer M;
    private List<ItemPlayer> ArrP;
    private int Id;
    private Integer V;
    private Integer AG;
    private Integer S ;
    
    public Integer getS() {
		return S;
	}

	public void setS(Integer s) {
		S = s;
	}

	public Integer getV() {
		return V;
	}

	public void setV(Integer v) {
		V = v;
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

	public List<ItemPlayer> getArrP() {
		return ArrP;
	}

	public void setArrP(List<ItemPlayer> arrP) {
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
