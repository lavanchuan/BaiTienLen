package TroChoiBaiTienLen.gameplay;

import java.io.IOException;

public class Time extends Thread{
    public static final long MILI = 1000;
    
    private int timer = 10;
    
    private boolean stoped = false;
    
    public void setTimer(int timer){
        this.timer = timer;
    }
    
    public boolean getStoped(){
        return this.stoped;
    }
    
    public void run(){
        try{
            sleep(timer * MILI);
            stoped = true;
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
        Time t  = new Time();
        t.start();
    }
}
