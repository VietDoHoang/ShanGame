package net.catte.log;

import java.util.HashMap;
import java.util.Map;

import com.dst.GameUtil;

public class PlayerAction {
	private String action;
    private String username;
    private int userId;
    private int tableId;
    private final Map<String, Object> options = new HashMap<>();
    
    public <T extends Object> PlayerAction(String action, String username, int userId, int tableId, T... options) {
        this.action = action;
        this.username = username;
        this.userId = userId;
        this.tableId = tableId;
        
        for(int i = 0 ; i < options.length; i++){
            String key = i == 0 ? "data" : "data" + i;
            this.options.put(key, options[i]);
        }
        
    }

    public <T extends Object> PlayerAction(String action, int userId, int tableId, T... options) {
        this.action = action;
        this.username = username;
        this.userId = userId;
        this.tableId = tableId;

        for(int i = 0 ; i < options.length; i++){
            String key = i == 0 ? "data" : "data" + i;
            this.options.put(key, options[i]);
        }

    }
    
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public Map<String, Object> getOptions() {
        return options;
    }
    
    
    @Override
    public String toString(){
        return GameUtil.gson.toJson(this);
    }
}
