package net.catte.logic.votranfer;

import java.io.Serializable;
import java.util.List;

import net.catte.logic.vo.Card;

public class PacketCard implements Serializable{

	public PacketCard(String evt, String username,List<Card> listCard) {
		// TODO Auto-generated constructor stub
		this.evt=evt;
		this.userName=username;
		this.listCard=listCard;
	}
	public PacketCard(String evt, String username, int Card) {
		this.evt=evt;
		this.userName=username;
		this.Card=Card;
	}
	private String evt;
	private int Card;
	private List<Card> listCard;
	private String userName;
	public int getCard() {
		 return Card;
	}

	public void setCountCard(int Card) {
		 this.Card = Card;
	}
	public List<Card> getListCard() {
		 return this.listCard;
	}
	 
	public void setCard(List<Card> listcard) {
		 this.listCard=listcard;
	}
	
	
}
