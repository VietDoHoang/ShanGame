package net.catte.table;

import org.apache.log4j.Logger;

import com.athena.services.api.ServiceContract;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.player.GenericPlayer;
import com.cubeia.firebase.api.game.player.PlayerStatus;
import com.cubeia.firebase.api.game.table.InterceptionResponse;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableListener;

import net.catte.logic.CatteBoard;
import net.catte.logic.vo.LocalEvt;
import net.catte.utils.EVT;

public class ZingTableListener implements TableListener {
	 private final static Logger _logger = Logger.getLogger(ZingTableListener.class);
	private ServiceContract serviceContract;
	public ZingTableListener(ServiceContract sc){
        this.serviceContract = sc;
    }
	@Override
	public void playerJoined(Table table, GenericPlayer player) {
		try {
            CatteBoard board = (CatteBoard)table.getGameState().getState();
            board.playerJoin(table, player.getPlayerId(), serviceContract);
        } catch(Exception e) {
            e.printStackTrace();
        }
	}

	@Override
	public void playerLeft(Table table, int playerId) {
		try{
			CatteBoard board = (CatteBoard)table.getGameState().getState();
            board.playerLeft(table, playerId, serviceContract);
        }catch(Exception e ){
            e.printStackTrace();
        }
	}

	@Override
	public void playerStatusChanged(Table table, int player, PlayerStatus status) {
		if(status.equals(PlayerStatus.DISCONNECTED) || status.equals(PlayerStatus.WAITING_REJOIN) || status.equals(PlayerStatus.LEAVING)){
            GameObjectAction goa = new GameObjectAction(table.getId());
            LocalEvt le = new LocalEvt();
            le.setEvt(EVT.CLIENT_DISCONNECT);
            le.setPid(player);
            goa.setAttachment(le);
            table.getScheduler().scheduleAction(goa, 0);
        }
	}

	@Override
	public void seatReserved(Table arg0, GenericPlayer arg1) {
		
	}

	@Override
	public void watcherJoined(Table arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void watcherLeft(Table arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
