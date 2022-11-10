package net.catte.logic.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.athena.services.vo.UserGame;

import net.catte.logic.votranfer.ItemPlayer;
import net.catte.logic.votranfer.ItemVPlayer;

public class Player {
    // info user
    private String Username;
    private Integer Userid;
    private Long AG = 0L;
    private Integer LQ;
    private Integer VIP;
    private int Avatar;
    private long FacebookId; // ID Facebook

    private short Usertype;
    private boolean Autoexit; // Tu dong thoat ban khi het van.
    private boolean Disconnect;
    private float CO;
    private float LQ0;
    private String displayName;
    private int keyObjectInGame;
    private long betMoney;
    // config for shan
    private boolean isBarker;
    private boolean isStart;
    private boolean isShan=false;
    private boolean finish=false;
    private UUID evtSS;
    // config for poker
    private List<Card> ArrCard;
    private UUID evtTimeOut;
    private int errorCode;
    private int[] arrIdCardDecided;

    
    public void resetPlayer(){
		ArrCard = new ArrayList<Card>();
		arrIdCardDecided = new int[6];
	}
    public Player(UserGame ui) {
    	setUserid(ui.getUserid());
    	setUsername(ui.getUsername());
    	setVIP((int)ui.getVIP());
    	setAvatar(ui.getA());
    	setAG(ui.getAG());
    	setLQ(ui.getLQ());
    	setCO(ui.getCO());
    	setLQ0(ui.getLQ0());
    	setFacebookId(ui.getFacebookid());
    	setUsertype(ui.getUsertype());
    	setDisplayName(ui.getDisplayName());
    	setDisconnect(false);
    	resetPlayer();
    }
    public ItemPlayer getItemPlayer() {
    	ItemPlayer item = new ItemPlayer();
    	item.setId(this.Userid);
    	item.setN(this.Username);
    	item.setAG(this.getAG());
    	item.setVIP(this.VIP);
    	item.setAv(this.Avatar);
    	item.setFId(this.FacebookId);
    	item.setDisplayName(this.displayName);
    	item.setKeyObjectInGame(this.keyObjectInGame);
    	return item;
    }
    
    public ItemVPlayer getItemVPlayer(boolean isUserView) {
    	ItemVPlayer item = new ItemVPlayer();
    	item.setId(this.Userid);
    	item.setN(this.Username);
    	item.setAG(this.getAG());
    	item.setVIP(this.VIP);
    	item.setAv(this.Avatar);
    	item.setFId(this.FacebookId);
    	item.setDisplayName(this.displayName);
    	item.setKeyObjectInGame(this.keyObjectInGame);
    	item.setArr(getIdCards(isUserView));
    	return item;
    }
    
    public int[] getIdCards(boolean isUserView){
        int[] arr = new int[ArrCard.size()];

        for(int i=0; i < ArrCard.size(); i++){
        	if(isUserView)
        		arr[i] = ArrCard.get(i).getI();
        	else {
        		arr[i] = 0;
        	}
        }
        return arr;
    }
	public String getUsername() {
		return Username;
	}
	public void setUsername(String username) {
		Username = username;
	}
	public Integer getUserid() {
		return Userid;
	}
	public void setUserid(Integer userid) {
		Userid = userid;
	}
	public Long getAG() {
		return AG;
	}
	public void setAG(Long aG) {
		AG = aG;
	}
	public Integer getLQ() {
		return LQ;
	}
	public void setLQ(Integer lQ) {
		LQ = lQ;
	}
	public Integer getVIP() {
		return VIP;
	}
	public void setVIP(Integer vIP) {
		VIP = vIP;
	}
	public int getAvatar() {
		return Avatar;
	}
	public void setAvatar(int avatar) {
		Avatar = avatar;
	}
	public long getFacebookId() {
		return FacebookId;
	}
	public void setFacebookId(long facebookId) {
		FacebookId = facebookId;
	}
	public short getUsertype() {
		return Usertype;
	}
	public void setUsertype(short usertype) {
		Usertype = usertype;
	}
	public boolean isAutoexit() {
		return Autoexit;
	}
	public void setAutoexit(boolean autoexit) {
		Autoexit = autoexit;
	}
	public boolean isDisconnect() {
		return Disconnect;
	}
	public void setDisconnect(boolean disconnect) {
		Disconnect = disconnect;
	}
	public float getCO() {
		return CO;
	}
	public void setCO(float cO) {
		CO = cO;
	}
	public float getLQ0() {
		return LQ0;
	}
	public void setLQ0(float lQ0) {
		LQ0 = lQ0;
	}
	public List<Card> getArrCard() {
		return ArrCard;
	}
	public void setArrCard(List<Card> arrCard) {
		ArrCard = arrCard;
	}
	public UUID getEvtTimeOut() {
		return evtTimeOut;
	}
	public void setEvtTimeOut(UUID evtTimeOut) {
		this.evtTimeOut = evtTimeOut;
	}
	public UUID getEVTSS() {
		return this.evtSS;
	}
	public void setEVTSS(UUID evtss) {
		this.evtSS=evtss;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public int[] getArrIdCardDecided() {
		return arrIdCardDecided;
	}
	public void setArrIdCardDecided(int[] arrIdCardDecided) {
		this.arrIdCardDecided = arrIdCardDecided;
	}
	public boolean isBarker() {
		return this.isBarker;
	}
	public void setBarket(boolean barker) {
		this.isBarker=barker;
	}
	public boolean isStart() {
		return isStart;
	}
	public void setStart(boolean isstart) {
		this.isStart=isstart;
	}
	public long getBetMoney() {
		return betMoney;
	}
	public void setBetMoney(long betmoney) {
		this.betMoney=betmoney;
	}
	public boolean isShan() {
		return isShan;
	}
	public void setIsShan(boolean shan) {
		this.isShan=shan;
	}
	public boolean isFinish() {
		return finish;
	}
	public void setFinish(boolean round2) {
		this.finish=round2;
	}
	
}
