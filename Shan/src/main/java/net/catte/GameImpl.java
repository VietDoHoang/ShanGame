package net.catte;

import com.athena.services.api.ServiceContract;
import com.cubeia.firebase.api.game.Game;
import com.cubeia.firebase.api.game.GameProcessor;
import com.cubeia.firebase.api.game.TableInterceptorProvider;
import com.cubeia.firebase.api.game.TableListenerProvider;
import com.cubeia.firebase.api.game.context.GameContext;
import com.cubeia.firebase.api.game.table.Table;
import com.cubeia.firebase.api.game.table.TableInterceptor;
import com.cubeia.firebase.api.game.table.TableListener;
import com.cubeia.firebase.api.server.SystemException;

import net.catte.table.ZingTableInterceptor;
import net.catte.table.ZingTableListener;

public class GameImpl implements Game, TableListenerProvider,TableInterceptorProvider{
	private ServiceContract serviceContract;
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void init(GameContext con) throws SystemException {
		this.serviceContract = con.getServices().getServiceInstance(ServiceContract.class);
		
	}

	public TableInterceptor getTableInterceptor(Table arg0) {
		return new ZingTableInterceptor(this.serviceContract);
	}

	public TableListener getTableListener(Table arg0) {
		return new ZingTableListener(this.serviceContract);
	}

	public GameProcessor getGameProcessor() {
		return new Processor(this);
	}
	
	public ServiceContract getServiceContract(){
        return serviceContract;
    }

}
