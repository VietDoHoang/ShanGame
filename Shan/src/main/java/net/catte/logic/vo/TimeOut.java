package net.catte.logic.vo;

public enum TimeOut {
	AUTO_START_GAME(2),
	START_GAME(5),
	TIME_OUT_TURN(5);
	private int value;
	private TimeOut(int value) {
		this.value = value;
	}
	
	public int getTimeInSecond() {
		return this.value;
	}
	
	public int getTimeInMiniSecond() {
		return this.value*1000;
	}
}
