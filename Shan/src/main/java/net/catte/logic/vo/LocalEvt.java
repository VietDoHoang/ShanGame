package net.catte.logic.vo;

public class LocalEvt {
	private String evt;
    private int pid;
    private TurnStatus turnStatus;
    private String keyChat; // for botChat

    public String getKeyChat() {
		return keyChat;
	}

	public void setKeyChat(String keyChat) {
		this.keyChat = keyChat;
	}
    
	public TurnStatus getTurnStatus() {
		return turnStatus;
	}

	public void setTurnStatus(TurnStatus turnStatus) {
		this.turnStatus = turnStatus;
	}

	/**
     * Get the value of pid
     *
     * @return the value of pid
     */
    public int getPid() {
        return pid;
    }

    /**
     * Set the value of pid
     *
     * @param pid new value of pid
     */
    public void setPid(int pid) {
        this.pid = pid;
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
}
