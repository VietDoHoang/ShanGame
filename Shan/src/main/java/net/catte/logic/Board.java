package net.catte.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.athena.services.api.ServiceContract;
import com.athena.services.vo.UserGame;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.LeaveAction;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.table.InterceptionResponse;
import com.cubeia.firebase.api.game.table.SeatRequest;
import com.cubeia.firebase.api.game.table.Table;
import com.dst.GameUtil;
import com.dst.MessageUtil;
import com.dst.common.TableLobbyAttributes;
import com.google.gson.JsonObject;

import net.catte.log.PlayerAction;
import net.catte.logic.vo.Card;
import net.catte.logic.vo.GameStatus;
import net.catte.logic.vo.LocalEvt;
import net.catte.logic.vo.Player;
import net.catte.logic.vo.TurnStatus;
import net.catte.logic.votranfer.Packet;
import net.catte.logic.votranfer.TableVInfo;
import net.catte.utils.EVT;
import net.catte.utils.ShanGameUtil;

public abstract class Board implements Serializable{
	
	public abstract void playerJoin(Table table, int playerId, ServiceContract serviceContract);

    public abstract void autoStartTable(Table table);

    public abstract void finish(Table table, ServiceContract serviceContract);

    public abstract void playerTimeout(Table table, ServiceContract serviceContract, int playerId);

    public abstract void tableTimeout(Table table, ServiceContract serviceContract);

    public abstract void playerLeft(Table table, int playerId, ServiceContract serviceContract);

    public abstract void requestBotJoin(Table table, ServiceContract serviceContract);

    public abstract void requestBotLeave(Table table, ServiceContract serviceContract);

    public abstract void startGame(Table table, ServiceContract serviceContract);

    public abstract void initBoard();
    
    public abstract void setBoard(int ownerId, int tid, Attribute[] att);
    
    public static final Logger logger = Logger.getLogger(Board.class); 
	public static final Object lock = new Object();
    public GameStatus gameStatus = GameStatus.WAIT_FOR_START;
    public Integer ownerId;
    public int tableId;
    public int minAG;
    public int MAX_PLAYER = 7;
    public boolean isFinished;
    public int player_id;
    /**
     * số người tối thiểu để bắt đầu
     */
    public int MIN_PLAYER = 2;
    public int id_barker;

    /**
     *
     */
    protected Date idleTime = new Date();
    public final static int TIME_WAIT_START = 5000;
    /**
     * mức cược bàn
     */
    protected int Mark;


//    protected int botTable = 0;

    /**
     * danh sách player ngồi trong bàn
     */
    protected List<Player> players = new ArrayList<>();
    protected List<Player> requestBanker = new ArrayList<>();

    /**
     * danh sách player ngồi xem
     */
    protected List<Player> viewPlayers = new ArrayList<>();
    
    protected List<Card> arrCardChia  = new ArrayList<>();;
    public static int GAMEID;
    public int currTurn;
    public int round;
    private boolean flagDestroy = false;
    
    public Board() {
        initBoard();
    }
    
    public InterceptionResponse allowJoin(Table table, SeatRequest request, ServiceContract serviceContract) {
    	try {
    		if (table.getAttributeAccessor().getIntAttribute(TableLobbyAttributes.STATED) == 1) {
            	logger.info(new PlayerAction("ALLOW_JOIN1", "", request.getPlayerId(), table.getId()));
                return new InterceptionResponse(false, -1);
            }
    		if(ownerId != request.getPlayerId()) {
    			UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(request.getPlayerId(), 0),
                            UserGame.class);
    			
    			if (ui == null) {
                	logger.info(new PlayerAction("ALLOW_JOIN2", "", request.getPlayerId(), table.getId()));
                    return new InterceptionResponse(false, -2);
                }
    			
                if (ui.getGameId() != table.getMetaData().getGameId()) {
                    serviceContract.sendErrorMsg(request.getPlayerId(), MessageUtil.getMessageResourceBundle("strNotGame", ui.getLanguage()));
                    logger.info(new PlayerAction("ALLOW_JOIN6", "", request.getPlayerId(), table.getId()));
                    return new InterceptionResponse(false, -6);
                }
                if (ui.getTableId() != 0 && ui.getTableId() != table.getId()) {
                	logger.info(new PlayerAction("ALLOW_JOIN7", "", request.getPlayerId(), table.getId()));
                    serviceContract.sendErrorMsg(request.getPlayerId(), MessageUtil.getMessageResourceBundle("strInGame", ui.getLanguage()));
                    return new InterceptionResponse(false, -7);
                }
                
                boolean allow = false;
                for(Player item : players){
            		if(item.getUserid() == request.getPlayerId()){
            			allow = true;
            		}
            	}
                
                if(!allow) {
                	if(ui.getAG() < minAG) {
                		return new InterceptionResponse(false, -12);
                	}
                	if(players.size() + viewPlayers.size() >= MAX_PLAYER){
                    	logger.info(new PlayerAction("ALLOW_JOIN13", "", request.getPlayerId(), table.getId()));
                    	return new InterceptionResponse(false, -13); //full table
                    }
                }
    		}
    	}catch(Exception e) {
    		logger.error(e.getMessage(), e);
    	}
    	return new InterceptionResponse(true, 0);
    }
    
    public InterceptionResponse allowLeave(Table table, int playerId) {
        synchronized (lock) {
            try {
                if (gameStatus == GameStatus.WAIT_FOR_START) {
                    return new InterceptionResponse(true, 0);
                } else {
                    for (int i = 0; i < viewPlayers.size(); i++) {
                        if (viewPlayers.get(i).getUserid() == playerId) {
                            return new InterceptionResponse(true, 0);
                        }
                    }
                    return new InterceptionResponse(false, 0);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                return new InterceptionResponse(false, -2);
            }
        }
    }
    
    public void setBoard(int pid, Attribute[] atts, ActivatorContext context, Table table) {
        synchronized (lock) {
        	GAMEID = table.getMetaData().getGameId();
            ownerId = pid;
            tableId = table.getId();

            for (Attribute att : atts) {
                switch (att.name) {
                    case TableLobbyAttributes.MARK:
                        Mark = att.value.getIntValue() < Mark ? Mark : att.value.getIntValue();
                        break;
                    case TableLobbyAttributes.AG:
                    	minAG = att.value.getIntValue();
                        break;
                    case TableLobbyAttributes.PLAYER:
                        MAX_PLAYER = att.value.getIntValue();
                        break;
                    case TableLobbyAttributes.NO_LIMITED:
                        break;
                    default:
                        break;
                }
            }

        }
    }
    
    public static List<Card> getRandomCard() {
        synchronized (lock) {
            try {
                List<Card> arrReturn = new ArrayList<Card>();
                List<Card> arrCard = new ArrayList<Card>();
                int num = 0;
                for (int i = 1; i < 5; i++) {
                    for (int j = 2; j < 15; j++) {
                        num++;
                        Card c = new Card(i, j, num);
                        arrCard.add(c);
                    }
                }
                int numCard = 52;
                for (int i = 0; i < numCard; numCard--) {
                    int j = GameUtil.random.nextInt(numCard);
                    arrReturn.add(arrCard.get(j));
                    arrCard.remove(j);
                }
                return arrReturn;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }

    
    public void chatTable(Table table, GameDataAction action) {
        try {
            for (int i = 0; i < players.size(); i++) {
                if (players.get(i).getUserid() == action.getPlayerId()) {
                    break;
                }
            }
            for (int i = 0; i < players.size(); i++) {
                    table.getNotifier().notifyPlayer(players.get(i).getUserid(), action);
            }
            for (int i = 0; i < viewPlayers.size(); i++) {
                    table.getNotifier().notifyPlayer(viewPlayers.get(i).getUserid(), action);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public void playerDisconnected(Table table, int playerId) {
        synchronized (lock) {
            try {
                if (gameStatus == GameStatus.WAIT_FOR_START) {
                    LeaveAction la = new LeaveAction(playerId, table.getId());
                    table.getScheduler().scheduleAction(la, 0);
                } else {
                    Player player = getPlayerById(playerId);
                    if (player != null) {
                        player.setDisconnect(true);

                    }

                    for (int i = 0; i < viewPlayers.size(); i++) {
                        if (viewPlayers.get(i).getUserid() == playerId) {
                            LeaveAction la = new LeaveAction(playerId, table.getId());
                            table.getScheduler().scheduleAction(la, 0);
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }
    
    public Player getPlayerById(int playerId) {
        for (Player player : players) {
            if (player.getUserid() == playerId) {
                return player;
            }
        }
        return null;
    }
    
    public void demo(JsonObject json) {
    	//dosome thing
    }
    
    public List<Integer> getArrId() {
        synchronized (lock) {
            List<Integer> arrId = new ArrayList<>();
            for (int j = 0; j < players.size(); j++) {
                arrId.add(players.get(j).getUserid());
            }
            for (int i = 0; i < viewPlayers.size(); i++) {
                arrId.add(viewPlayers.get(i).getUserid());
            }
            return arrId;
        }
    }
    
    protected TableVInfo getTableForDis(Table table, int pid, long ag) {
        synchronized (lock) {
            try {
                TableVInfo ret = new TableVInfo();
                ret.setCN(players.get(currTurn).getUsername());
                ret.setCT(
                        20 - Integer.parseInt(String.valueOf(GameUtil.getSecondsBetween2Dates(new Date(), idleTime))));
                if (ret.getCT() < 1) {
                    ret.setCT(1);
                }
                ret.setId(table.getId());
                ret.setM(this.Mark);
                ret.setS(MAX_PLAYER);
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getUserid() == pid) {
                        ret.getArrP().add(players.get(i).getItemVPlayer(true));
                    } else {
                        ret.getArrP().add(players.get(i).getItemVPlayer(false));
                    }
                }
                return ret;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }
    
    protected TableVInfo getVTable() {
    	synchronized (lock) {
    		try {
    			TableVInfo ret = new TableVInfo();
    			ret.setCN(players.get(currTurn).getUsername());
                ret.setCT(
                        20 - Integer.parseInt(String.valueOf(GameUtil.getSecondsBetween2Dates(new Date(), idleTime))));
                if (ret.getCT() < 1) {
                    ret.setCT(1);
                }
                ret.setId(tableId);
                ret.setM(this.Mark);
                ret.setS(MAX_PLAYER);
                for (int i = 0; i < players.size(); i++) {
                	ret.getArrP().add(players.get(i).getItemVPlayer(false));
                }
                return ret;
    		}catch(Exception e) {
    			logger.error(e.getMessage(), e);
    		}
    		return null;
		}
    }
    
    private void updateTableDestroy(){
    	if(!flagDestroy){
//	    	for (int i = 0; i < ActivatorImpl.tables.length; i++){
//	            if(ActivatorImpl.tables[i].getTableId() == tableId){
//	            	_logger.info("set new att "+ tableId);
//	            	AttributeValue Destroy = new AttributeValue(1);
//	            	ActivatorImpl.tables[i].getAttributes().put("Cancel", Destroy);
//	            	flagDestroy = true;
//	            	break;
//	            }
//	            	
//	    	}
    	}
    }
    
    protected GameObjectAction genGameObjectAction(String evt, int pid, int tid, TurnStatus turnStatus) {
		synchronized (lock) {
			GameObjectAction goa = new GameObjectAction(tid);
			LocalEvt packet = new LocalEvt();
			packet.setEvt(evt);
			if(turnStatus != null)
				packet.setTurnStatus(turnStatus);
			packet.setPid(pid);
			goa.setAttachment(packet);
			return goa;
		}
	}

	public List<Player> getPlayers() {
		return players;
	}
    public int getMark() {
    	return Mark;
    }
    public int getPlayerId() {
    	return player_id;
    }


    
    
    
}
