package net.catte;

import org.apache.log4j.Logger;

import com.cubeia.firebase.api.action.GameDataAction;
import com.cubeia.firebase.api.action.GameObjectAction;
import com.cubeia.firebase.api.game.GameProcessor;
import com.cubeia.firebase.api.game.table.Table;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.catte.logic.CatteBoard;
import net.catte.logic.vo.LocalEvt;
import net.catte.utils.EVT;
import net.catte.utils.ShanFunction;
import net.catte.utils.ShanGameUtil;

public class Processor implements GameProcessor{
	private final JsonParser parser = new JsonParser();
    private final GameImpl game;
    public static final Logger _logger = Logger.getLogger(Process.class);
    
    public Processor(GameImpl game) {
        this.game = game;
    }
	@Override
	public void handle(GameDataAction action, Table table) {
		// TODO Auto-generated method stub
		String message = new String(action.getData().array());
		JsonObject je = (JsonObject)parser.parse(message);
		CatteBoard board = (CatteBoard)table.getGameState().getState();
		_logger.info("evt comming");
		//{"evt":"bet","ag":100}
		if(je.get("evt") !=null) {
			String evt = je.get("evt").getAsString();
			switch (evt) {
			case EVT.DATA_READDY:
				board.ReadyTable(this.game.getServiceContract(), table, action.getPlayerId());
				break;
			case EVT.DATA_EXIT_GAME:
				break;
			case EVT.DATA_KICK_TABLE:
				int pidKickPlayer = board.KickTable(action.getPlayerId());
				board.KickTable(action, table);  
				break;
				/*
			case EVT.DATA_START_BETS_MONEY:
				board.startBetsMoney(this.game.getServiceContract(),table,action.getPlayerId());
				break;
				*/
			case EVT.CLIENT_BETS_MONEY:
				//{"evt","username","betsmoney"}
				long betmoney = je.get("betmoney").getAsLong();
				board.betsMoney(betmoney,this.game.getServiceContract(),table,action.getPlayerId(),betmoney);
				break;
			case EVT.DATA_TAKE_CARD:
				//{"evt":"takeCard","take":"Yes/No"}
				String take = je.get("take").getAsString();
				board.takeCard(this.game.getServiceContract(),table,action.getPlayerId(),take);
				break;
			case EVT.DATA_SEND_REQUEST_BECOME_BANKER:
				// {"evt:", "userid", "request"}
				Boolean request = je.get("request").getAsBoolean();
				board.sendResquestBecomeBanker(this.game.getServiceContract(),table,action.getPlayerId(),request);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void handle(GameObjectAction goa, Table table) {
		// TODO Auto-generated method stub 
		LocalEvt data = (LocalEvt) goa.getAttachment();
		CatteBoard board = (CatteBoard)table.getGameState().getState();
		String event = data.getEvt();
		switch (event) {
		case EVT.AUTO_START_GAME:
			board.startGame(table, game.getServiceContract(), goa);
			break;
		case EVT.OBJECT_FINESHED:
			board.finishedGame(this.game.getServiceContract(), table);
			break;
		case EVT.TIMES_OUT_TAKE_CARD:
			board.timesOutTakeCard(table, this.game.getServiceContract());
		case EVT.TIMES_OUT_BETS:
			board.timesOutBets(table, this.game.getServiceContract());
		case EVT.DATA_SECOND_TURN:
			board.secondTurn(this.game.getServiceContract(),table,goa);
		default:
			break;
		}
		
	}
}
