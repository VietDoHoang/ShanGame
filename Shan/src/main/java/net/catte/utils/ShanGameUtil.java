package net.catte.utils;

import java.util.Random;

import com.dst.GameUtil;
import com.google.gson.Gson;



public class ShanGameUtil extends GameUtil{
	//private ActionTransformer trans;
    public static final Gson gson = new Gson();
    public static final Random random = new Random();
    
    public static final String strSystem = "System";
    public static final String strLeftTable = " get out of table.";
    public static final int MAX_PLAYER = 7;
    public static final int SOLO_PLAYER = 2;
}
