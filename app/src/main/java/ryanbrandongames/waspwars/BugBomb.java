package ryanbrandongames.waspwars;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.Random;

/**
 * Created by B on 1/16/2016.
 */
public class BugBomb {
    static final Random rand = new Random();
    private Rect bugBombRect, smokeRect;
    private Bitmap bugBomb, smoke, smoke2;
    private int bugBombX, bugBombY, bugBombExplode, lastBombTime, bombEndTime, bombEndInterval;
    private int bombSpawnTime, bombMaxSpawn, bombMinSpawn, bombMaxEnd, bombMinEnd;
    private int smokeX, smokeY, smokeTime, smokeAnimateTime;
    private boolean showBomb, showSmoke;

    public BugBomb() {
        this.bugBomb            = null;
        this.bugBombRect        = null;
        this.smokeRect          = null;
        this.smoke              = null;
        this.smoke2             = null;
        this.bugBombX           = 0;
        this.bugBombY           = 0;
        this.bugBombExplode     = -1;
        this.lastBombTime       = 0;
        this.bombEndTime        = 0;
        this.bombEndInterval    = 0;
        this.bombSpawnTime      = 0;
        this.bombMaxSpawn       = 15; //45
        this.bombMinSpawn       = 5; //25
        this.bombMaxEnd         = 10;
        this.bombMinEnd         = 5;
        this.smokeX             = 0;
        this.smokeY             = 0;
        this.smokeTime          = 0;
        this.showBomb           = false;
        this.showSmoke          = false;
    }

    public Bitmap getBugBomb(){
        return this.bugBomb;
    }

    public void setBugBomb(Bitmap bb){
        this.bugBomb = bb;
    }

    public Rect getBugBombRect(){
        return this.bugBombRect;
    }

    public void setBugBombRect(){
        this.bugBombRect = new Rect(this.bugBombX, this.bugBombY, this.bugBombX + this.bugBomb.getWidth(), this.bugBombY + this.bugBomb.getHeight());
    }

    public void nullifyBugBombRect(){
        this.bugBombRect = null;
    }

    public Rect getSmokeRect(){
        return this.smokeRect;
    }

    public void setSmokeRect(){
        this.smokeRect = new Rect(this.smokeX, this.smokeY, this.smokeX + this.smoke.getWidth(), this.smokeY + this.smoke.getHeight());
    }

    public void nullifySmokeRect(){
        this.smokeRect = null;
    }

    public Bitmap getSmoke(){
        return this.smoke;
    }

    public void setSmoke(Bitmap s){
        this.smoke = s;
    }

    public Bitmap getSmoke2(){
        return this.smoke2;
    }

    public void setSmoke2(Bitmap s2){
        this.smoke2 = s2;
    }

    public int getBugBombX(){
        return this.bugBombX;
    }

    public void setBugBombX(int bbx){
        this.bugBombX = bbx;
    }

    public int getBugBombY(){
        return this.bugBombY;
    }

    public void setBugBombY(int bby){
        this.bugBombY = bby;
    }

    public int getBugBombExplode(){
        return this.bugBombExplode;
    }

    public void setBugBombExplode(int bbe){
        this.bugBombExplode = bbe;
    }

    public int getLastBombTime(){
        return this.lastBombTime;
    }

    public void setLastBombTime(int lbt){
        this.lastBombTime = lbt;
    }

    public int getBombEndTime(){
        return this.bombEndTime;
    }

    public void setBombEndTime(int bet){
        this.bombEndTime = bet;
    }

    public int getBombEndInterval(){
        return this.bombEndInterval;
    }

    public void setBombEndInterval(){
        this.bombEndInterval = rand.nextInt(this.bombMaxEnd - this.bombMinEnd) + this.bombMinEnd;
    }

    public int getBombSpawnTime(){
        return this.bombSpawnTime;
    }

    public void setBombSpawnTime(){
        this.bombSpawnTime = rand.nextInt(this.bombMaxSpawn- this.bombMinSpawn) + this.bombMinSpawn;
    }

    public int getSmokeX(){
        return this.smokeX;
    }

    public void setSmokeX(int sx){
        this.smokeX = sx;
    }

    public int getSmokeY(){
        return this.smokeY;
    }

    public void setSmokeY(int sy){
        this.smokeY = sy;
    }

    public int getSmokeTime(){
        return this.smokeTime;
    }

    public void setSmokeTime(int st){
        this.smokeTime = st;
    }

    public int getSmokeAnimateTime(){
        return this.smokeAnimateTime;
    }

    public void setSmokeAnimateTime(int sat){
        this.smokeAnimateTime = sat;
    }

    public boolean showBomb(){
        return this.showBomb;
    }

    public void setShowBomb(boolean sb){
        this.showBomb = sb;
    }

    public boolean showSmoke(){
        return this.showSmoke;
    }

    public void setShowSmoke(boolean ss){
        this.showSmoke = ss;
    }
}