package net.catte.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.catte.logic.SortCardAScByHS;
import net.catte.logic.vo.Card;

public class ShanFunction {
	
	public static int getPoint(List<Card> listcard) {
		int point=0;
		for(int i=0;i<listcard.size();i++) {
			point += listcard.get(i).getPointCard();
		}
		return getPoint(point);
	}
	
	public static int getPoint(int point) {
		if(point<0) {
			return (Integer) null;
		}else if(point>=0 && point<=10) {
			return point;
		}else if(point>=10 && point <20) {
			return (point-10);
		}else if(point>=20 && point<30){
			return (point-20);
		}else {
			return (Integer) null;
		}
	}
	
	public static String check_Shan(List<Card> listcard) {
		if(listcard.size()==2) {
			int point = listcard.get(0).getPointCard()+listcard.get(1).getPointCard();
			if(point==9 || point ==8) {
				if(listcard.get(0).getN()==listcard.get(1).getN()) {
					return "ShanDoi";
				}else if(listcard.get(0).getS()==listcard.get(1).getS()) {
					return "ShanDongChat";
				}else {
					return "Shan";
				}
		}
		}
		return null;
		/*
		if(listcard.size()==3) {
			int point = listcard.get(0).getPointCard()+listcard.get(1).getPointCard() + listcard.get(2).getPointCard();
		
			if(point==8 || point ==9) {
				if()
			}
		}
		return null;
		*/
	}
	public static String check_Xam(List<Card> listcard) {
		if(listcard.size()==3) {
		if(listcard.get(0).getN()==listcard.get(1).getN() && listcard.get(1).getN()==listcard.get(2).getN()) {
			return "Xam";
		}
		}
		return null;
	}
	
	public static String check_Thung_Pha_Sanh(List<Card> listCards) {
		Collections.sort(listCards, new SortCardAScByHS());
		if (listCards.size()==3 && check_Di(listCards) ==null) {
			if(listCards.get(0).getN()==(listCards.get(1).getN()-1) && listCards.get(0).getN() == (listCards.get(2).getN()-2)) {
				return "Thung Pha Sanh";
			}
		}
		return null;
	}
	
	public static String check_Di(List<Card> listcard) {
		if(listcard.size()==3) {
			if(check_Xam(listcard)==null) {
				if(listcard.get(0).isDi() && listcard.get(1).isDi() && listcard.get(2).isDi() ) {
					if(listcard.get(0).getS()== listcard.get(1).getS() && listcard.get(0).getS()==listcard.get(2).getS()) {
						return "Di_Dong_Chat";
					}
					return "Di";
				}
			}
		}
		return null;
	}
	
	public static int gettotalPoint(List<Card> listcard) {
		int total=0;
		for(int i =0;i<listcard.size();i++) {
			total += listcard.get(i).getPointCard();
			getPoint(total);
		}
		return total;	

	}
	
	public static Card getHighestCard(List<Card> listcard) {
		Collections.sort(listcard, new SortCardAScByHS());
		int lastindex = listcard.size()-1;
		return listcard.get(lastindex);
	}
	
	// true la player thagn , false la player thua
	public static String equalDeckCard(List<Card> listplayer, List<Card> listbanker) {
		// player la shan
		if(check_Shan(listplayer)!=null) {
			if(check_Shan(listbanker)==null) {
				return check_Shan(listplayer);
			}else {
				if(getHighestCard(listplayer).getCardHS()>getHighestCard(listbanker).getCardHS()) {
					return check_Shan(listplayer);
				}else if(getHighestCard(listplayer).getCardHS()==getHighestCard(listbanker).getCardHS()) {
					if(getHighestCard(listplayer).getS()>getHighestCard(listbanker).getS())
						return check_Shan(listplayer);
					return null;
				}else {
					return null;
				}
			}
		}else if(check_Xam(listplayer)!=null) {   //player la XAM
			// khong can check listbanker la shan vi neu listbanker la shan thi moi player chi co 2 la
			if(check_Xam(listbanker)!=null) {
				return check_Xam(listplayer);
			}else {
				if(listplayer.get(0).getCardHS()>listbanker.get(0).getCardHS()) {
					return check_Xam(listplayer);
				}else {
					return null;
				}
			}
		}else
		// player la Di
		if(check_Di(listplayer)!=null) {
			// khong can check listbanker la SHAN vi neu listbanker la SHAN thi moi player chi co 2 la
			if(check_Di(listbanker)==null) {
				if(check_Xam(listbanker)==null) {
					return check_Di(listplayer);
				}else {
					return null;
				}
			}else {
				return "draw";
			}
		}else
		//player la Thung Pha Sanh
		if(check_Thung_Pha_Sanh(listplayer)!=null) {
			// khong can check listbanker la SHAN vi neu listbanker la SHAN thi moi player chi co 2 la
			if(check_Thung_Pha_Sanh(listbanker)==null) {
				if(check_Di(listbanker) ==null || check_Xam(listbanker)==null) {
					
					return check_Thung_Pha_Sanh(listplayer);
					
				}else {
					return null;
				}
			}else {
				if(getHighestCard(listplayer).getCardHS()>getHighestCard(listbanker).getCardHS()) {
					return check_Thung_Pha_Sanh(listplayer);
				}else if(getHighestCard(listplayer).getCardHS()==getHighestCard(listbanker).getCardHS()) {
					if(getHighestCard(listplayer).getS()>getHighestCard(listbanker).getS()) {
						return check_Thung_Pha_Sanh(listplayer);
					}else {
						return null;
					}
				}else {
					return null;
				}
			}
		}else {
			// tính điểm thông thường
			if(check_Shan(listbanker)!=null || check_Xam(listbanker)!=null ||check_Di(listbanker)!=null||check_Thung_Pha_Sanh(listbanker)!=null) {
				return null;
			}
			if(gettotalPoint(listplayer)>gettotalPoint(listbanker)) {
				return "more point";
			}else if(gettotalPoint(listbanker)==gettotalPoint(listplayer)) {
				if(listplayer.size()<listbanker.size()) {
					return "more point";
				}else if(listbanker.size()==listplayer.size()){
					if(getHighestCard(listplayer).getCardHS()>getHighestCard(listbanker).getCardHS()) {
						return "more point";
					}else if(getHighestCard(listplayer).getCardHS()==getHighestCard(listbanker).getCardHS()) {
						if(getHighestCard(listbanker).getS()<getHighestCard(listplayer).getS()) {
							return "more point";
						}else {
							return null;
						}
					}else {
						return "more point";
					}
				}
			}
		}
		return null;
		
	}
	
	public static void main(String[] args) {
		List<Card> list1 = new ArrayList<>();
		list1.add(new Card(3,4));
		list1.add(new Card(3,2));
		list1.add(new Card(3,3));
		list1.add(new Card(3,1));
		for(Card p : list1) {
			System.out.println(p.getDisplay());
		}
		Collections.sort(list1, new SortCardAScByHS());
		for(Card p : list1) {
			System.out.println(p.getDisplay());
		}
		ShanFunction a = new ShanFunction();
		System.out.println(a.check_Thung_Pha_Sanh(list1));
	}
	
}
