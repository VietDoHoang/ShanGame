package net.catte.table;

import org.apache.log4j.Logger;

import com.athena.services.api.ServiceContract;
import com.cubeia.firebase.api.game.table.InterceptionResponse;
import com.cubeia.firebase.api.game.table.SeatRequest;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableInterceptor;

import net.catte.logic.CatteBoard;

public class ZingTableInterceptor implements TableInterceptor{
	private final static Logger _logger = Logger.getLogger(ZingTableListener.class);
	private ServiceContract serviceContract;
    public ZingTableInterceptor(ServiceContract service){
        this.serviceContract = service;
    }
	@Override
	public InterceptionResponse allowJoin(Table table, SeatRequest request) {
		try{
			CatteBoard board = (CatteBoard)table.getGameState().getState();
	        return board.allowJoin(table,request,this.serviceContract);
	    }catch(Exception ex){
	        _logger.error(ex.getMessage(), ex);
	        return new InterceptionResponse(false, -2);
	    }
	}
	@Override
	public InterceptionResponse allowLeave(Table table, int playerId) {
		try{
            CatteBoard board = (CatteBoard) table.getGameState().getState();
            return board.allowLeave(table,playerId);
        }catch(Exception ex){
            _logger.error(ex.getMessage(), ex);
            return new InterceptionResponse(false, -2);
        }
	}
	@Override
	public InterceptionResponse allowReservation(Table arg0, SeatRequest arg1) {
		return new InterceptionResponse(true, 0);
	}

}
