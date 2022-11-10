package net.catte.logic;

import java.util.Comparator;

import net.catte.logic.vo.Card;


public class SortCardAScByHS implements Comparator<Card> {

	@Override
	public int compare(Card o1, Card o2) {
		// TODO Auto-generated method stub
		if(o1.getCardHS() > o2.getCardHS())
			return 1;
		else if(o1.getCardHS() == o2.getCardHS()){
			if(o1.getS() > o2.getS()){
				return 1;
			} else {
				return -1;
			}
		} else 
			return -1;
	}


}
