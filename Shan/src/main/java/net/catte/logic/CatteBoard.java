package net.catte.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.athena.services.api.ServiceContract;
import com.athena.services.vo.UserGame;
import com.cubeia.firebase.api.action.Action;
import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.action.LeaveAction;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.game.table.Table;
import com.dst.GameUtil;
import com.dst.MessageUtil;
import com.dst.common.CommonConstant;
import com.dst.common.TableLobbyAttributes;

import net.catte.log.PlayerAction;
import net.catte.logic.vo.Card;
import net.catte.logic.vo.GameStatus;
import net.catte.logic.vo.LocalEvt;
import net.catte.logic.vo.Player;
import net.catte.logic.vo.TimeOut;
import net.catte.logic.votranfer.EvtLeaveTable;
import net.catte.logic.votranfer.Packet;
import net.catte.logic.votranfer.TableInfo;
import net.catte.logic.votranfer.TableVInfo;
import net.catte.utils.EVT;
import net.catte.utils.ShanFunction;
import net.catte.utils.ShanGameUtil;

public class CatteBoard extends Board implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CatteBoard.class);
	private static final Logger LOG_IFRS = Logger.getLogger("IFRS_LOG_POKER");
	
	@Override
	public void initBoard() {
		this.arrCardChia = new ArrayList<Card>();
		this.players = new ArrayList<Player>();
	}
	
	@Override
	public void setBoard(int ownerId, int tid, Attribute[] atts) {
		this.ownerId = ownerId;
		this.tableId = tid;
		
		for(Attribute att : atts) {
			if(att.name.contains(TableLobbyAttributes.MARK)) {
				this.Mark = att.value.getIntValue();
			}else if(att.name.contains(TableLobbyAttributes.AG)) {
				this.minAG = att.value.getIntValue();
			}
		}
	}
	
	@Override
	public void startGame(Table table, ServiceContract serviceContract) {
		try {
			if(gameStatus != gameStatus.WAIT_FOR_START) {
				return ;
			}
			if(players.size()<MIN_PLAYER || players.size()>MAX_PLAYER) {
				return;
			}
			for(Player p : players) {
				if(!p.isStart()) {
					return;
				}
			}
			
			// bat dau van dau
			round++;
			gameStatus = gameStatus.STARTED;
			for(Player p: players) {
				if(p.getUserid()==id_barker) {
					p.setBarket(true);
				}
			}
			// ĐẶT CƯỢC
			GameObjectAction action = new GameObjectAction(table.getId());
			action.setAttachment(EVT.DATA_BETS_MONEY);
			table.getScheduler().scheduleAction(action, 2000);
			// chia bài
			arrCardChia = getRandomCard();
			divideTurnOneCard();
			// check barker SHan
			if(ShanFunction.check_Shan(players.get(getIndexBarker()).getArrCard())!=null){
				gameStatus = gameStatus.FINISHED;
				for(Player p : players) {
					p.setFinish(true);
				}
				GameObjectAction action1 = new GameObjectAction(table.getId());
				action.setAttachment(EVT.CLIENT_FINISHED);
				table.getScheduler().scheduleAction(action, 2000);
				return;
			}
			// gửi thông báo nếu người chơi là bài pok
			for(Player p : players) {
				if(ShanFunction.check_Shan(p.getArrCard())!=null && p.getUserid()!= id_barker) {
					p.setIsShan(true);
					Packet packet = new Packet(EVT.LIST_CARD_SHAN, p.getUsername(), p.getArrCard(), ShanFunction.getPoint(p.getArrCard()));
					table.getNotifier().notifyAllPlayers(ShanGameUtil.toDataAction(getPlayerId(), table.getId(), packet));
				}
			}
			
			gameStatus= gameStatus.SECONDTURN;
			
			
			
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void playerJoin(Table table, int playerId, ServiceContract serviceContract) {
		synchronized (lock) {
			try {
				UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(playerId, table.getId()),
						UserGame.class);
				if (ui != null) {
					if (gameStatus != GameStatus.WAIT_FOR_START) {
						for (int i = 0; i < players.size(); i++) {
							Player playerR = players.get(i);
							if (playerR.getUserid().intValue() == ui.getUserid().intValue()) {
								playerR.setDisconnect(false);

								TableVInfo tableVInfo = getTableForDis(table, playerId, playerR.getAG());

								table.getNotifier().notifyPlayer(playerId,
										GameUtil.toDataAction(playerId, table.getId(),
												new Packet(EVT.CLIENT_RECONNECT_TABLE, GameUtil.gson.toJson(tableVInfo))));
								return;
							}
						}
					}
					
					if(players.size() == 1 && playerId == ownerId) {
						Packet packet = new Packet(EVT.CLIENT_CREATE_TABLE, GameUtil.gson.toJson(getTable()));
                        table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(), packet));
                        
                        LeaveAction la = new LeaveAction(players.get(0).getUserid(), table.getId());
						table.getScheduler().scheduleAction(la, CommonConstant.TIME_WAIT_PLAYER_JOIN);
						
						confirmRoom(serviceContract, playerId, table.getId());
					}else if(players.size() + viewPlayers.size() < this.MAX_PLAYER) {
						Player player = getPlayerById(playerId);
						if(gameStatus == GameStatus.WAIT_FOR_START) {
							players.add(player);
							
							table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(), new Packet(EVT.CLIENT_OTHER_JOIN, GameUtil.gson.toJson(getTable()))));
                            table.getNotifier().notifyAllPlayersExceptOne(GameUtil.toDataAction(playerId, table.getId(), new Packet(EVT.CLIENT_JOIN_TABLE, GameUtil.gson.toJson(player.getItemPlayer()))), playerId);
						
						}else {
							viewPlayers.add(player);
                            table.getNotifier().notifyPlayer(playerId, GameUtil.toDataAction(playerId, table.getId(), new Packet(EVT.CLIENT_VIEW_TABLE, GameUtil.gson.toJson(getVTable()))));
						}
						
						confirmRoom(serviceContract, playerId, table.getId());
					}
					
					
					table.getAttributeAccessor().setStringAttribute(TableLobbyAttributes.ARR_ID,
							GameUtil.gson.toJson(getArrId()));
					table.getAttributeAccessor().setIntAttribute(TableLobbyAttributes.SEATED,
							players.size() + viewPlayers.size());
				}
			}catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		
	}
	
	public void confirmRoom(ServiceContract service, int uid, int tableid) {
        service.confirmSelectRoom_Only(uid, 0, tableid, this.Mark);
    }
	
	private int getErrorCode(int pid){
    	for(Player p : players){
    		if(p.getUserid() == pid){
    			return p.getErrorCode();
    		}
    	}
    	return 0;
    }
	
	@Override
	public void playerLeft(Table table, int playerId, ServiceContract serviceContract) {
		synchronized (lock) {
            try {
            	if (gameStatus == GameStatus.WAIT_FOR_START) {
            		serviceContract.PlayerLeaveTable(playerId, false);
            		if (players.size() == 1 && viewPlayers.size() == 0) {
            			int errorCode = getErrorCode(playerId);
            			EvtLeaveTable packet = new EvtLeaveTable(EVT.CLIENT_LEFT, String.valueOf(players.get(0).getUsername()), errorCode);
						table.getNotifier().notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), packet));
						 logger.info(new PlayerAction(EVT.CLIENT_LEFT, "", playerId, table.getId(),
						 packet));
						 
						 table.getScheduler().cancelAllScheduledActions();
						 table.getAttributeAccessor().setIntAttribute(TableLobbyAttributes.STATED, 1);
            		} else {
	        			for (int i = 0; i < players.size(); i++) {
							if (players.get(i).getUserid() == playerId) {
								int errorCode = getErrorCode(playerId);
								EvtLeaveTable packet = new EvtLeaveTable(EVT.CLIENT_LEFT, players.get(i).getUsername(), errorCode);
								table.getNotifier()
										.notifyAllPlayers(GameUtil.toDataAction(playerId, table.getId(), packet));
								 logger.info(new PlayerAction(EVT.CLIENT_LEFT, "", 0, table.getId(),
								 packet));
								players.remove(i);
								
								if (playerId == ownerId && this.players.size() > 0)
									ownerId = this.players.get(0).getUserid();
								break;
							}
						}
	        			
	        			//add viewPlayer
	        			if(players.size() > 0) {
	        				pushViewPlayersIntoPlay(table, serviceContract);
	        			}else {
	        				table.getScheduler().cancelAllScheduledActions();
							table.getAttributeAccessor().setIntAttribute(TableLobbyAttributes.STATED, 1);
	        			}
            		}
            	}
            	for (int i = 0; i < viewPlayers.size(); i++) {
					if (viewPlayers.get(i).getUserid() == playerId) {
						serviceContract.PlayerLeaveTable(playerId, false);

						EvtLeaveTable packet = new EvtLeaveTable(EVT.CLIENT_LEFT, viewPlayers.get(i).getUsername(), viewPlayers.get(i).getErrorCode());
						table.getNotifier().notifyPlayer(playerId,
								GameUtil.toDataAction(playerId, table.getId(), packet));
						 logger.info(new PlayerAction(EVT.CLIENT_LEFT,
						 viewPlayers.get(i).getUsername(), playerId, table.getId(), packet));

						viewPlayers.remove(i);
						break;
					}
				}
            	
            	table.getAttributeAccessor().setStringAttribute(TableLobbyAttributes.ARR_ID,
						GameUtil.gson.toJson(getArrId()));
				table.getAttributeAccessor().setIntAttribute(TableLobbyAttributes.SEATED,
						players.size() + viewPlayers.size());
            }catch(Exception e) {
            	logger.error(e.getMessage(), e);
            }
		}
		
	}
	
	public void pushViewPlayersIntoPlay(Table table, ServiceContract serviceContract) {
		for (int i = players.size(); i < MAX_PLAYER; i++) {
			if (viewPlayers.size() > 0) {
				Player viewPlayer = viewPlayers.get(0);
				viewPlayers.remove(0);
				players.add(viewPlayer);
	
				Packet packet2 = new Packet(EVT.CLIENT_JOIN_TABLE, GameUtil.gson.toJson(viewPlayer.getItemPlayer()));
				table.getNotifier().notifyAllPlayersExceptOne(GameUtil.toDataAction(0, table.getId(), packet2),
						viewPlayer.getUserid());
				logger.info(new PlayerAction(EVT.CLIENT_JOIN_TABLE, "", viewPlayer.getUserid(), table.getId(), packet2));
	
				Packet packet3 = new Packet(EVT.CLIENT_OTHER_JOIN, GameUtil.gson.toJson(getTable()));
				table.getNotifier().notifyPlayer(viewPlayer.getUserid(), GameUtil.toDataAction(0, table.getId(), packet3));
				logger.info(new PlayerAction(EVT.CLIENT_OTHER_JOIN, viewPlayer.getUsername(), viewPlayer.getUserid(),
						table.getId(), packet3));
			}
		}
	}
	
	private TableInfo getTable() {
        synchronized (lock) {
            try {
                TableInfo ti = new TableInfo();
                ti.setId(tableId);
                ti.setM(this.Mark);
                ti.setAG(this.minAG);
                ti.setS(this.MAX_PLAYER);
                for (int i = 0; i < players.size(); i++) {
                    ti.getArrP().add(players.get(i).getItemPlayer());
                }
                return ti;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            return null;
        }
    }
	public void ReadyTable(ServiceContract serviceContract, Table table, int userid) {
		synchronized (lock) {
			try {
				for (int i = 0; i < players.size(); i++) {
					if ((players.get(i).getUserid() == userid) && !players.get(i).isStart()) {
						if (players.get(i).getEVTSS() != null) {
							table.getScheduler().cancelScheduledAction(players.get(i).getEVTSS());
						}
						players.get(i).setStart(true);
						Packet readyTable = new Packet(EVT.DATA_READDY, players.get(i).getUsername());
						table.getNotifier().notifyAllPlayers(ShanGameUtil.toDataAction(userid, table.getId(), readyTable));
						logger.info(ShanGameUtil.gson.toJson(readyTable));
					}
				}

				if (players.size() >= 2)
					StartCountTimeOwner(table);
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}
    private void StartCountTimeOwner(Table table) { // Number Readied = Total
		// Player ==> Start Time for
		// Owner
		synchronized (lock) {
			try {
				if (gameStatus != GameStatus.WAIT_FOR_START) {
					return;
				}
				int dem = 0;
				for (int j = 0; j < players.size(); j++) {
					if (players.get(j).isStart()) {
						dem++;
					}
				}
				if (players.get(0).getEVTSS() != null) {
					table.getScheduler().cancelScheduledAction(players.get(0).getEVTSS());
					players.get(0).setEVTSS(null);
				}

				// System.out.println("dem "+dem+" - size "+listPlayer.size());
				if (dem == players.size() && (players.size() >= 1)) {

					GameObjectAction goa = new GameObjectAction(table.getId());
					LocalEvt le = new LocalEvt();
					le.setEvt(EVT.OBJECT_AUTO_START);
					le.setPid(players.get(0).getUserid());
					goa.setAttachment(le);
					table.getScheduler().scheduleAction(goa, TIME_WAIT_START);
				}
			} catch (Exception e) {
				// handle exception
				logger.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void autoStartTable(Table table) {
		GameObjectAction goa = genGameObjectAction(EVT.AUTO_START_GAME, 0, table.getId(), null);
		table.getScheduler().scheduleAction(goa, TimeOut.AUTO_START_GAME.getTimeInMiniSecond());
	}

	@Override
	public void finish(Table table, ServiceContract serviceContract) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerTimeout(Table table, ServiceContract serviceContract, int playerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tableTimeout(Table table, ServiceContract serviceContract) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestBotJoin(Table table, ServiceContract serviceContract) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestBotLeave(Table table, ServiceContract serviceContract) {
		// TODO Auto-generated method stub
		
	}


	// chia bai cho user
	private void divideTurnOneCard() {
		for(int i=0;i<2;i++) {
			for(Player p : players) {
				p.getArrCard().add(arrCardChia.get(0));
				arrCardChia.remove(0);
			}
		}
	}
	private void takeCardFromListCard(int playerid) {
		players.get(getIndexPlayer(playerid)).getArrCard().add(arrCardChia.get(0));
		arrCardChia.remove(0);
	}
	private boolean checkFinish() {
		for(Player p : players) {
			if(!p.isFinish()) {
				return false;
			}
		}
		return true;
		
	}

	public void finishedGame(ServiceContract servicecontract, Table table) {
		// TODO Auto-generated method stub
		try {
			if(gameStatus==gameStatus.FINISHED && checkFinish()) {
			table.getScheduler().cancelAllScheduledActions();
			isFinished = true;
			int indexBarker = 0;
			for(int i=0;i<players.size();i++) {
				if(players.get(i).isBarker()) {
					indexBarker=i;
					break;
				}
			}
			// CheckWin va gui thong bao
			for(int i=0;i<players.size();i++) {
				if(i==indexBarker)
					continue;
				if(ShanFunction.equalDeckCard(players.get(i).getArrCard(), players.get(indexBarker).getArrCard())!=null) {
					Packet packet = new Packet(EVT.CLIENT_FINISHED, players.get(i).getUsername(),
							ShanFunction.equalDeckCard(players.get(i).getArrCard(), players.get(indexBarker).getArrCard()) );
					table.getNotifier().notifyPlayer(players.get(i).getUserid(), ShanGameUtil.toDataAction(players.get(i).getUserid(),
							table.getId(), packet));
					table.getNotifier().notifyPlayer(players.get(i).getUserid(), ShanGameUtil.toDataAction(players.get(i).getUserid(),
							table.getId(), packet));
										
				}else {
					Packet packet = new Packet(EVT.CLIENT_FINISHED, players.get(i).getUsername(),
							"LOST" );
					table.getNotifier().notifyPlayer(players.get(i).getUserid(), ShanGameUtil.toDataAction(players.get(i).getUserid(),
							table.getId(), packet));
					table.getNotifier().notifyPlayer(players.get(i).getUserid(), ShanGameUtil.toDataAction(players.get(i).getUserid(),
							table.getId(), packet));
				}
			}
			// set banker
			if(players.get(indexBarker).getAG()<Mark) {
				players.get(indexBarker).setBarket(false);
				indexBarker=0;
				if(requestBanker.size()==0) {
					players.get(indexBarker).setBarket(false);
					Packet packet = new Packet(EVT.OBJECT_EMPTY_BANKER,"trả banker về cho server");
					table.getNotifier().notifyAllPlayers(ShanGameUtil.toDataAction(0, table.getId(), packet));
					id_barker=0;
				}else {
					int id_new_barker=requestBanker.get(0).getUserid();
					
					for(int i=1;i<requestBanker.size();i++) {
						if(requestBanker.get(i).getAG()>requestBanker.get(getIndexPlayer(id_new_barker)).getAG()) {
							id_new_barker = requestBanker.get(i).getUserid();
						}
					}
					id_barker= id_new_barker;
					players.get(getIndexPlayer(id_new_barker)).setBarket(true);
					Packet packet = new Packet(EVT.CLIENT_NEW_BANKER, players.get(getIndexBarker()).getUsername(),
							players.get(getIndexBarker()).getAG(),round+1);
				}
				
			}else {
			if(requestBanker.size()!=0) {
				int id_new_barker=requestBanker.get(0).getUserid();
				
				for(int i=1;i<requestBanker.size();i++) {
					if(requestBanker.get(i).getAG()>requestBanker.get(getIndexPlayer(id_new_barker)).getAG()) {
						id_new_barker = requestBanker.get(i).getUserid();
					}
				}
				if(requestBanker.get(getIndexPlayer(id_new_barker)).getAG()>players.get(indexBarker).getAG()) {
					players.get(getIndexBarker()).setBarket(false);
					id_barker=id_new_barker;
					players.get(getIndexBarker()).setBarket(true);
					Packet packet = new Packet(EVT.CLIENT_NEW_BANKER, players.get(getIndexBarker()).getUsername(),
							players.get(getIndexBarker()).getAG(),round+1);
					
				}
			}
			}
			resetTable(table);
			}else {
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		}
		
	}

	private void resetTable(Table table) {
		// TODO Auto-generated method stub
		this.gameStatus=gameStatus.WAIT_FOR_START;
		arrCardChia.clear();
		isFinished=false;
		currTurn=0;
		requestBanker.clear();
	}

	public void betsMoney(long betmoney,ServiceContract serviceContract, Table table, int playerId) {
		// TODO Auto-generated method stub
		try {
			int index = getIndexPlayer(playerId);
			players.get(index).setBetMoney(betmoney);
			if(betmoney<Mark) {
				players.get(index).setBetMoney(Mark);
			}
			// check so tien toi da duoc dat
			if(betmoney>players.get(getIndexBarker()).getAG()) {
				players.get(index).setBetMoney(players.get(getIndexBarker()).getAG());
			}else if(betmoney>players.get(index).getAG()){
				players.get(index).setBetMoney(players.get(index).getAG());
			}
			Packet packet = new Packet(EVT.DATA_BETS_MONEY, players.get(index).getUsername(), players.get(index).getBetMoney()+"");
			table.getNotifier().notifyAllPlayers(ShanGameUtil.toDataAction(playerId, table.getId(),packet ));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		}
	}
	public int getIndexPlayer(int playerId) {
		for(int i=0;i<players.size();i++) {
			if(playerId==players.get(i).getUserid()) {
				return i;
			}
		}
		return playerId;
	}
	public int getIndexBarker() {
		for(int i=0;i<players.size();i++) {
			if(players.get(i).getUserid()==id_barker) {
				return i;
			}
		}
		return 0;
		
	}

	public void takeCard(ServiceContract serviceContract, Table table, int playerId, Boolean take) {
		// TODO Auto-generated method stub
		try {
			if(gameStatus != gameStatus.SECONDTURN) {
				return;
			}else {
			if(!take) {
				players.get(getIndexPlayer(playerId)).setFinish(true);
				return;
			}else {
				// chia thêm lá bài với người chơi chọn nhận thêm
				takeCardFromListCard(playerId);
				players.get(getIndexPlayer(playerId)).setFinish(true);
			}
			}	
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		}
		
	}

	public void sendResquestBecomeBanker(ServiceContract serviceContract, Table table, int playerId,boolean request) {
		// TODO Auto-generated method stub
		try {
			if(!request) {
				return;
			}else {
				Packet packet = new Packet(EVT.DATA_SEND_REQUEST_BECOME_BANKER, playerId, players.get(getIndexPlayer(playerId)).getUsername());
				table.getNotifier().notifyAllPlayers(ShanGameUtil.toDataAction(playerId, table.getId(), packet));
				requestBanker.add(players.get(getIndexPlayer(playerId)));
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage(),e);
		}
		
	}
	public int KickTable(int userid) {
		if(gameStatus == gameStatus.WAIT_FOR_START) {
			for(int i=0;i<players.size();i++) {
				if( players.get(i).getUserid()==userid) {
					players.remove(i);
					return players.get(i).getUserid();
				}
			}
		}
		return 0;
	}

	public void KickTable(GameDataAction action, Table table) {
		// TODO Auto-generated method stub
		CatteBoard board = (CatteBoard) table.getGameState().getState();
		int pidKickPlayer = board.KickTable(action.getPlayerId());
		if(pidKickPlayer !=0) {
			LeaveAction la = new LeaveAction(pidKickPlayer,table.getId());
			table.getScheduler().scheduleAction(la, 0);
			Packet pkk= new Packet(EVT.DATA_KICK_TABLE,MessageUtil.getMessageResourceBundle(pidKickPlayer+ " đã bị đuổi") );
			table.getNotifier().notifyPlayer(pidKickPlayer, ShanGameUtil.toDataAction(pidKickPlayer, table.getId(), pkk));
			table.getNotifier().notifyAllPlayers(ShanGameUtil.toDataAction(pidKickPlayer, table.getId(), pkk));
		}else {
			
			table.getNotifier().notifyAllPlayers(ShanGameUtil.toDataAction(pidKickPlayer, table.getId(), new Packet(EVT.DATA_KICK_TABLE, "đuổi player không thành công")));
		}
	}

}