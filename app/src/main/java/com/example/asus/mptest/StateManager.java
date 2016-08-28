package com.example.asus.mptest;

/**
 * Created by ASUS on 2016/8/21.
 * 测试用
 */
public class StateManager {
    private static final int PLAYING_STATE = 0;
    private static final int PAUSE_STATE = 1;

    private static Song currentSong=null;
    private static int currentState=PAUSE_STATE;

    public static void setPlayingState(){
        currentState=PLAYING_STATE;
    }
    public static void setPauseState(){
        currentState=PAUSE_STATE;
    }
    public static void setCurrentSong(Song song){
        currentSong=song;
    }
    public static Song getCurrentSong(){
        return currentSong;
    }
    public static int getCurrentState(){
        return currentState;
    }
}
