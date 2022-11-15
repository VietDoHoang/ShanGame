package net.catte.logic.votranfer;

import java.io.Serializable;
import java.util.List;

import net.catte.logic.vo.Card;

public class PacketCard implements Serializable{

	public PacketCard(String username, int countcard,List<Card> card) {
		// TODO Auto-generated constructor stub
		this.userName=username;
		this.countCard=countcard;
		this.card=card;
	}
	private int countCard;
	private List<Card> card;
	private String userName;
	public int getCountCard() {
		 return countCard;
	}

	public void setCountCard(int countcard) {
		 this.countCard = countcard;
	}
	public List<Card> getCard() {
		 return this.card;
	}
	 
	public void setCard(List<Card> card) {
		 this.card=card;
	}
	
	
}
