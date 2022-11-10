package net.catte;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.athena.services.api.ServiceContract;
import com.athena.services.vo.UserGame;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.common.AttributeValue;
import com.cubeia.firebase.api.common.AttributeValue.Type;
import com.cubeia.firebase.api.game.activator.ActivatorContext;
import com.cubeia.firebase.api.game.activator.CreationRequestDeniedException;
import com.cubeia.firebase.api.game.activator.GameActivator;
import com.cubeia.firebase.api.game.activator.RequestAwareActivator;
import com.cubeia.firebase.api.game.activator.RequestCreationParticipant;
import com.cubeia.firebase.api.game.lobby.LobbyTable;
import com.cubeia.firebase.api.routing.ActivatorAction;
import com.cubeia.firebase.api.routing.RoutableActivator;
import com.cubeia.firebase.api.server.SystemException;
import com.dst.GameUtil;
import com.dst.bean.MarkCreateTable;
import com.dst.common.ActivatorEVT;
import com.dst.common.TableLobbyAttributes;
import com.dst.constants.CatteConstant;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.catte.table.ZingParticipant;

public class ActivatorImpl implements GameActivator,RequestAwareActivator ,RoutableActivator{
	
	public static final Logger logger = Logger.getLogger(ActivatorImpl.class);
	public static ServiceContract serviceContract;
	public static int GAMEID;
	private final Object threadLock = new Object();
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> future;
    public static ActivatorContext context;
    public static final int MAX_PLAYER = 4;

  private final JsonParser parser = new JsonParser();

  public static LobbyTable[] tables = null;

	public void init(ActivatorContext con) throws SystemException {
		// TODO Auto-generated method stub
		ActivatorImpl.serviceContract = con.getServices().getServiceInstance(ServiceContract.class);
        ActivatorImpl.context = con;
        GAMEID = con.getGameId();
        tables = context.getTableFactory().listTables();
        setupThreading();
//        serverConfig.loadConfig(serviceContract.getConfigLuckyBot(GAMEID));
	}
	
	public void start() {
		startThreading();
	}

	public void stop() {
		stopThreading();
		
	}

	public void onAction(ActivatorAction<?> action) {
		try {
			 String message = (String) action.getData();
			JsonObject jo = (JsonObject) parser.parse(message);
			String strUi = serviceContract.getUserInfoByPid(jo.get("pid").getAsInt(), 0);
			if(strUi.length() == 0) {
				ActivatorImpl.serviceContract.sendErrorMsg(jo.get("pid").getAsInt(), "null");
			}else {
				UserGame ui = GameUtil.gson.fromJson(strUi, UserGame.class);
                String evt = jo.get("evt").getAsString();
                switch(evt) {
                	case ActivatorEVT.SELECT_GAME:
                		getMarkForView(ui.getUserid());
                		break;
                	case ActivatorEVT.SEARCHT:
                		searchTable(ui, jo);
                		break;
                	case ActivatorEVT.PLAY_NOW_CHANGE_TABLE: 
                		break;
                	case ActivatorEVT.CREATE_TABLE:
                		createTable(ui, jo);
                		break;
                	case ActivatorEVT.BOT_CREATE_TABLE:
                		break;
                	default:
                		break;
                		
                }
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void playNowChangeTable(UserGame ui, JsonObject jo) {
		try {
			int markSearch = jo.get("M").getAsInt();
            long maxAg = CatteConstant.getMaxChipJoin(markSearch);
            
            if(ui.getAG() > maxAg && maxAg != 0){
                serviceContract.sendToClient(ui.getUserid(), "10", "You have many chips now. Let's choose other tables with higher bets to enjoy more!");
            }else  if (ui.getAG() < CatteConstant.getMinChipJoin(markSearch)) {
                ActivatorImpl.serviceContract.sendToClient(ui.getUserid(), "500", "no chip");
                return;
            }else {
            	int idtable = FindFastTableId(CatteConstant.getMarkCreateTableByMark(markSearch).getMark(), ui.getVIP(), jo.get("idtable").getAsInt());
            	if (idtable > 0) {
                	logger.info("FindFastTableId2 "+ ui.getUserid() + " id table "+ idtable + ", mark "+ markSearch);
                    ActivatorImpl.serviceContract.AutoJoinTable(ui.getUserid(), idtable, GAMEID);
                } else {
                    createTableSiam(markSearch, ui.getUserid(), "", "");
                }
            }
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	public void createTable(UserGame ui, JsonObject jo) {
		try {
			String P =  (jo.has("P")) ? jo.get("P").getAsString() : "";
            String N = (jo.has("N")) ? jo.get("N").getAsString() : ""; // table name
            long markMax = CatteConstant.getMaxChipJoin(jo.get("M").getAsInt());
            if(ui.getAG() > markMax && markMax != 0 && ui.getVIP() > 0){
                serviceContract.sendToClient(ui.getUserid(), "10", "You have many chips now. Let's choose other tables with higher bets to enjoy more!");
            }else{
            	createTableSiam(jo.get("M").getAsInt(), jo.get("pid").getAsInt(), P,N);
            }
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public void searchTable(UserGame ui, JsonObject jo) {
		try {
			List<MarkCreateTable> lstMark = CatteConstant.getListMark();
			if (ui.getAG() < lstMark.get(0).getMinAgCon()) {
                ActivatorImpl.serviceContract.sendToClient(jo.get("pid").getAsInt(), "10", "no chip");
            } else if(ui.getTableId() != 0) {
                ActivatorImpl.serviceContract.sendToClient(jo.get("pid").getAsInt(), "10", "You're in another game");
            } else {
            	 int idtable = 0;
                 int markS = lstMark.get(0).getMark();
                 
                 for(int  i = lstMark.size() - 1; i>= 0 ; i--){
                     if(ui.getAG() >= lstMark.get(i).getAg()){
                         markS = lstMark.get(i).getMark();
                         break;
                     }
                 }
                 
                 idtable = FindFastTableId(markS, ui.getVIP(), 0);
                 
                 if (idtable > 0) {
                     ActivatorImpl.serviceContract.AutoJoinTable(jo.get("pid").getAsInt(), idtable, GAMEID);
                 } else {
                     createTableSiam(markS, jo.get("pid").getAsInt(), "", "");
                 }
            }
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private void createTableSiam(int mark, int pid, String pri, String N) {
        try {
            Attribute[] temp = new Attribute[4];
            temp[0] = new Attribute(TableLobbyAttributes.GAME_ID, new AttributeValue(GAMEID));
            temp[1] = new Attribute(TableLobbyAttributes.NAME, new AttributeValue(N));
            temp[2] = new Attribute(TableLobbyAttributes.MARK, new AttributeValue(mark));
            temp[3] = new Attribute(TableLobbyAttributes.P, new AttributeValue(pri));
            ActivatorImpl.context.getTableFactory().createTable(MAX_PLAYER, getParticipantForRequest(pid, MAX_PLAYER, temp));
        } catch (CreationRequestDeniedException e) {
            logger.error(e.getMessage(), e);
        }
    }
	
	private int FindFastTableId(int mark, int vip, int idtable) {
        try {
            int ret = 0;
            if (tables != null) {
                List<Integer> ListTemp = new ArrayList<>();
                int count = 0;
                for (int i = 0; i < tables.length; i++) {
                    LobbyTable table = tables[i];
                    if(table.getAttributes().containsKey(TableLobbyAttributes.CANCEL)){
                        continue;
                    }
                    AttributeValue markTable = table.getAttributes().get(TableLobbyAttributes.MARK);
                    
                    if (markTable.getIntValue() != mark) {
                        continue;
                    }
                    
                    if (table.getAttributes().get(TableLobbyAttributes.P).getStringValue().length() >= 1) {
                        continue;
                    }
                    
                    if (table.getAttributes().get(TableLobbyAttributes.STATED).getIntValue() == 1) {
                        continue;
                    }
                    
                    if (idtable > 0 && table.getTableId() == idtable) {
                        continue;
                    }
                    
                    AttributeValue seated = table.getAttributes().get(TableLobbyAttributes.SEATED);
                    if(seated.getIntValue() >= MAX_PLAYER)
                        continue;
                    
                    ListTemp.add(table.getTableId());
                    count++;
                    
                    if(count == 10) break;
                }
                if (ListTemp.size() > 0) {
                    int a = GameUtil.random.nextInt(ListTemp.size());
                    ret = ListTemp.get(a);
                }
                return ret;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return 0;
    }
	
	public void getMarkForView(int pid) {
        try {
            serviceContract.sendToClient(pid, ActivatorEVT.LIST_MARK_TABLE_VIEW, GameUtil.gson.toJson(CatteConstant.getListMark()));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
	
	public void destroy() {
		destroyThreading();
		
	}

	public RequestCreationParticipant getParticipantForRequest(int pid, int seats, Attribute[] attributes) throws CreationRequestDeniedException {
		if (attributes.length < 1) {
            throw new CreationRequestDeniedException(1);
        }
		
		UserGame ui = GameUtil.gson.fromJson(serviceContract.getUserInfoByPid(pid, 0), UserGame.class);
		if(ui == null)  throw new CreationRequestDeniedException(1);
		
		return new ZingParticipant(pid, attributes, ui);
	}
	
	private void setupThreading() {
        synchronized (threadLock) {
            if (scheduler != null) {
                return; // SANITY CHECK
            }
            scheduler = Executors.newScheduledThreadPool(2, new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("TableActivator");
                    thread.setDaemon(true);
                    return thread;
                }
            });
        }
    }
	
	private void startThreading() {
        synchronized (threadLock) {
            if (scheduler == null) {
                return; // SANITY CHECK
            }
            if (future != null) {
                stopThreading();
            }
            future = scheduler.scheduleWithFixedDelay(new Runner(), 10, 10, TimeUnit.SECONDS);
//            futureBot = scheduler.scheduleWithFixedDelay(new BotRunner(), 10, 5, TimeUnit.SECONDS);
            
        }
    }
	
	private void stopThreading() {
        synchronized (threadLock) {
            if (future == null) {
                return; // SANITY CHECK
            }
            future.cancel(true);
            future = null;
//            futureBot.cancel(true);
//            futureBot = null;
        }
    }
	
	private void destroyThreading() {
        synchronized (threadLock) {
            if (scheduler == null) {
                return; // SANITY CHECK
            }
            if (future != null) {
                stopThreading();
            }
            scheduler.shutdownNow();
            scheduler = null;
        }
    }
	
	private class Runner implements Runnable {

        public void run() {
            try {
                checkTables();
//                Thread.sleep(1000);
            } catch (Throwable th) {
                logger.error(th.getMessage(), th);
            }
        }
    }
	
	protected void checkTables() {
        try {
            tables = ActivatorImpl.context.getTableFactory().listTables();
            destroyTables();
            updateCurrPlayer();
            tables = ActivatorImpl.context.getTableFactory().listTables();
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
	
	private void destroyTables() {
        for(int i = tables.length - 1; i >= 0; i--) {
            LobbyTable table = tables[i];
            if(isEmpty(table)) {
            	logger.info("destroy empty table "+ table.getTableId());
                ActivatorImpl.context.getTableFactory().destroyTable(table.getTableId(), true);
            }else if(isLongTimeTable(table) ){
            	logger.info("destroy logtime table "+ table.getTableId());
                ActivatorImpl.context.getTableFactory().destroyTable(table.getTableId(), true);
            }
        }
    }
   
    private boolean isEmpty(LobbyTable t) {
        AttributeValue att = t.getAttributes().get(TableLobbyAttributes.STATED);
        int attSEATED = t.getAttributes().get(TableLobbyAttributes.SEATED).getIntValue();
        if(attSEATED == 0) {
        	return true;
        }
        if (att == null || att.getType() != Type.INT) {
            return false;
        } else {
            return att.data.equals(new Integer(1));
        }
    }
    
   private boolean isLongTimeTable(LobbyTable table) {
	   AttributeValue lastConnect = null;
	   long secondsCheck = 0;
	   AttributeValue ArrId = null;
       lastConnect = table.getAttributes().get(TableLobbyAttributes.LAST_CONNECT);

       secondsCheck = (new java.util.Date()).getTime() / 1000 - lastConnect.getDateValue().getTime() / 1000;
       	if (secondsCheck > 500) {
           ArrId = table.getAttributes().get(TableLobbyAttributes.ARR_ID);
           if (ArrId != null) {
               List<Double> arrId = GameUtil.gson.fromJson(ArrId.getStringValue(), ArrayList.class);
               for (int j = 0; j < arrId.size(); j++) {
                   ActivatorImpl.serviceContract.PlayerLeaveTable(arrId.get(j).intValue(), false);
               }
           }
           return true;
       }
       	return false;
   }
   
   private void updateCurrPlayer() {
	   
   }

}
