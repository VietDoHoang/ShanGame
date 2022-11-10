package net.catte.logic.votranfer;

public class EvtLeaveTable {

	public EvtLeaveTable(String evt,String Name, int errorCode){
        this.evt = evt;
        this.Name = Name;
        this.errorCode = errorCode;
    }
    private String evt;
    private String Name;
    private int errorCode;
    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return Name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String Name) {
        this.Name = Name;
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

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
    
}
