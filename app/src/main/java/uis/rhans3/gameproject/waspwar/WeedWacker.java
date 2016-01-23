package uis.rhans3.gameproject.waspwar;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.Random;

public class WeedWacker {
    static final Random rand = new Random();
    private int weedWackerX , weedWackerY, lastWWTime, wwEndTime, wwEndInterval, wwSpawnTime;
    private int wwMaxSpawn, wwMinSpawn, wwMaxEnd, wwMinEnd;
    private boolean showWW;
    private Bitmap weedWacker;
    private Rect wwRect;

    public WeedWacker(){
        this.weedWacker     = null;
        this.wwRect         = null;
        this.weedWackerX    = 0;
        this.weedWackerY    = 0;
        this.lastWWTime     = 0;
        this.wwEndTime      = 0;
        this.wwEndInterval  = 0;
        this.wwSpawnTime    = 0;
        this.wwMaxSpawn     = 40;
        this.wwMinSpawn     = 25;
        this.wwMaxEnd       = 7;
        this.wwMinEnd       = 4;
        this.showWW         = false;
    }

    public Bitmap getWeedWacker(){
        return this.weedWacker;
    }

    public void setWeedWacker(Bitmap ww){
        this.weedWacker = ww;
    }

    public Rect getWWRect(){
        return this.wwRect;
    }

    public void setWWRect(){
        this.wwRect = new Rect(this.weedWackerX, this.weedWackerY, this.weedWackerX + this.weedWacker.getWidth(), this.weedWackerY + this.weedWacker.getHeight());
    }

    public void nullifyWWRect(){
        this.wwRect = null;
    }

    public int getWeedWackerX(){
        return this.weedWackerX;
    }

    public void setWeedWackerX(int wwx){
        this.weedWackerX = wwx;
    }

    public int getWeedWackerY(){
        return this.weedWackerY;
    }

    public void setWeedWackerY(int wwy){
        this.weedWackerY = wwy;
    }

    public void setLastWWTime(int lwwt){
        this.lastWWTime = lwwt;
    }

    public int getLastWWTime(){
        return this.lastWWTime;
    }

    public int getWWEndTime(){
        return this.wwEndTime;
    }

    public void setWWEndTime(int wwet){
        this.wwEndTime = wwet;
    }

    public int getWWEndInterval(){
        return this.wwEndInterval;
    }

    public void setWWEndInterval(){
        this.wwEndInterval = rand.nextInt(this.wwMaxEnd - this.wwMinEnd) + this.wwMinEnd;
    }

    public int getWWSpawnTime(){
        return this.wwSpawnTime;
    }

    public void setWWSpawnTime(){
        this.wwSpawnTime = rand.nextInt(this.wwMaxSpawn - this.wwMinSpawn) + this.wwMinSpawn;
    }

    public boolean showWeedWacker(){
        return this.showWW;
    }

    public void setShowWeedWacker(boolean sww){
        this.showWW = sww;
    }
}
