package net.catte.logic.vo;

import java.util.ArrayList;
import java.util.List;


public class Card implements Comparable<Card>{

    public Card() {
        this.S = 0;
        this.N = 0;
        this.I = 0;
    }

    public Card(int s, int n, int i) {
        this.S = s;
        this.N = n;
        this.I = i;
    }

    public Card(int n, int s){
		this.N = n;
		this.S = s;
		int x = n == 1 ? 13:(n - 1);
		this.I = (s - 1) * 13 + x;
	}
    
    public Card(int i) {
        this.I = i;
        if (i == 60 || i == 61) {
            this.S = this.N = i;
        } else {
            this.S = (i - 1) / 13 + 1;
            this.N = (i - 1) % 13 + 2;
        }
    }
    
    public static void main(String[] args) {
    	Card card1 = new Card(14,3);
    	Card card2 = new Card(1,4);
    	
    	System.out.println(card1.getDisplay());
    	System.out.println(card2.getDisplay());
    	System.out.println(card1.compareTo(card2));
		List<Card> a = new ArrayList<Card>();
		for(int i=1;i<53;i++) {
			a.add(new Card(i));
		}
		for(Card p : a) {
			System.out.println(p.getDisplay());
		}
	}

    private int S;
    private int N;
    private int I;

    public int getI() {
        return I;
    }

    public void setI(int i) {
        I = i;
    }

    public int getS() {
        return S;
    }

    public void setS(int s) {
        S = s;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }
    
    public String getDisplay() {
        String number = "";
        String suit = "";
        switch (S) {
            case 4:
                suit = "♠";
                break;
            case 1:
                suit = "♣";
                break;
            case 3:
                suit = "♦";
                break;
            case 2:
                suit = "♥";
                break;
            default:
                break;
        }

        switch (N) {
            case 14:
                number = "A";
                break;
            case 13:
                number = "K";
                break;
            case 12:
                number = "Q";
                break;
            case 11:
                number = "J";
                break;
            default:
                number = Integer.toString(N);
                break;
        }

        return number + suit;
    }

    @Override
    public int compareTo(Card card) {

        int tempN = this.getCardHS();
        int temp1N = card.getCardHS();
        if(tempN == temp1N)
        	return this.getS() > card.getS() ? 1 : -1;
        return tempN > temp1N ? 1 : -1;
    }
    
    public int getPointCard() {
    	switch (this.N) {
		case 10:
			return 0;
		case 11:
			return 0;
		case 12:
			return 0;
		case 13:
			return 0;
		case 14:
			return 1;
		default:
			return this.N;
		}
    }
    
    public boolean equals(Card card){
    	if(card == null) return false;
    	if(!(card instanceof Card)) return false;
    	if(card.getN() == this.getN() && card.getS() == this.getS())
    		return true;
    	return false;
    }
    
    public SuitCard getSuit() {
        switch (this.getS()) {
            case 1:
                return SuitCard.CLUBS;
            case 2:
                return SuitCard.HEARTS;
            case 3:
                return SuitCard.DIAMONDS;
            case 4:
                return SuitCard.SPADES;

            default:
                return SuitCard.HEARTS;
        }

    }
    
    
    public int getCardHS(){
    	return this.N;
    }
    public boolean isDi() {
    	if(this.getPointCard()==0 && this.getN()!=10) {
    		return true;
    	}
    	return false;
    }
    

    @Override
    public String toString() {
        return getDisplay();
    }
}
