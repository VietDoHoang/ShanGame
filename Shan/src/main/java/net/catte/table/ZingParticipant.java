package net.catte.table;

import java.util.Date;

import org.apache.log4j.Logger;

import com.athena.services.vo.UserGame;
import com.cubeia.firebase.api.action.JoinRequestAction;
import com.cubeia.firebase.api.common.Attribute;
import com.cubeia.firebase.api.game.GameDefinition;
import com.cubeia.firebase.api.game.activator.RequestCreationParticipant;
import com.cubeia.firebase.api.game.lobby.LobbyTableAttributeAccessor;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.lobby.LobbyPath;
import com.dst.GameUtil;
import com.dst.common.TableLobbyAttributes;

import net.catte.logic.CatteBoard;
import net.catte.logic.vo.Player;

public class ZingParticipant implements RequestCreationParticipant{
	
	Logger logger = Logger.getLogger(ZingParticipant.class);
    private int pid;
    private Attribute[] atts;
    private UserGame uinfo;
	public ZingParticipant(int pid, Attribute[] att, UserGame uinfo) {
        this.pid = pid;
        this.atts = att;
        this.uinfo = uinfo;
    }
	
	@Override
	public void tableCreated(Table table, LobbyTableAttributeAccessor accessor) {
		CatteBoard board = new CatteBoard();
		board.setBoard(pid,table.getId(), atts);
		Player owner = new Player(uinfo);
		board.getPlayers().add(owner);
		table.getGameState().setState(board);
		
		accessor.setIntAttribute(TableLobbyAttributes.STATED, 0);
		accessor.setDateAttribute(TableLobbyAttributes.LAST_CONNECT, new Date());
		accessor.setIntAttribute(TableLobbyAttributes.START_GAME, 0);
		accessor.setStringAttribute(TableLobbyAttributes.ARR_ID, GameUtil.gson.toJson(board.getArrId()));
		accessor.setIntAttribute(TableLobbyAttributes.MARK, board.getMark());
		accessor.setIntAttribute(TableLobbyAttributes.SEATED, 1);
		JoinRequestAction join = new JoinRequestAction(pid, table.getId(), 0, owner.getUsername(), "");
        table.getScheduler().scheduleAction(join, 0);
		
	}
	@Override
	public LobbyPath getLobbyPathForTable(Table table) {
		// TODO Auto-generated method stub
		return new LobbyPath(table.getMetaData().getGameId(), "T_" + table.getId(), table.getId());
	}
	@Override
	public String getTableName(GameDefinition gameDefinition, Table table) {
		// TODO Auto-generated method stub
		return "poker[" + table.getId() + "]";
	}
	@Override
	public int[] modifyInvitees(int[] invitees) {
		return invitees;
	}
	@Override
	public boolean reserveSeatsForInvitees() {
		return true;
	}
}
