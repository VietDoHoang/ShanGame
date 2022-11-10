package net.catte.logic.vo;

public enum SuitCard {
	 CLUBS(1), //Tep
     DIAMONDS(3),
     HEARTS(2),
     SPADES(4); //Bich

     private final int value;

     private SuitCard(int value){
         this.value = value;
     }

     public int getValue() {
         return value;
     }
}
