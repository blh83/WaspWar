package ryanbrandongames.waspwars;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.Random;

public class Hive {
	static final Random rand = new Random();
	public int hiveX, hiveY, brokenHiveX, brokenHiveY;
	private Rect hiveRect, brokenHiveRect;
    private boolean showHive, showBrokenHive;
    private int lastHiveTime, hiveEndTime, hiveEndInterval, hiveSpawnTime, hiveMaxSpawn, hiveMinSpawn, hiveMaxEnd;
    private int hiveMinEnd, brokenHiveTime, maxHiveWasp, minHiveWasp;
    public int hiveBreaking, hiveBreakingInitial;
    private Bitmap hive, brokenhive1, brokenhive2, brokenhive3;

    public Hive(){
        this.lastHiveTime           =   0;
		this.hiveEndTime            =   0;
		this.hiveMaxSpawn           =   15;
		this.hiveMinSpawn           =   5;
		this.hiveMaxEnd             =   20;
		this.hiveMinEnd             =   10;
        this.brokenHiveTime         =   0;
		this.maxHiveWasp            =   5;
		this.minHiveWasp            =   2;
		this.hiveBreaking           =   -1;
		this.hiveBreakingInitial    =   -1;
        this.showHive               =   false;
        this.showBrokenHive         =   false;
        this.hive                   =   null;
        this.brokenhive1            =   null;
        this.brokenhive2            =   null;
        this.brokenhive3            =   null;
        this.hiveRect               =   null;
        this.brokenHiveRect         =   null;
        setHiveSpawnTime();
        setHiveEndInterval();
	}

    public Rect getHiveRect(){
        return this.hiveRect;
    }

    public void setHiveRect(){
        this.hiveRect = new Rect(this.hiveX, this.hiveY, this.hiveX + this.hive.getWidth(), this.hiveY + this.hive.getHeight());
    }

    public void nullifyHiveRect(){
        this.hiveRect = null;
    }

    /*
    public Rect getBrokenHiveRect(){
        return this.brokenHiveRect;
    }

    public void setBrokenHiveRect(){
        this.brokenHiveRect = new Rect(this.brokenHiveX, this.brokenHiveY, this.brokenHiveX + this.brokenhive1.getWidth(), this.brokenHiveY + this.brokenhive1.getHeight());
    }
    */

    public void nullifyBrokenHiveRect(){
        this.brokenHiveRect = null;
    }

    public void setBrokenhive1(Bitmap hive)
    {
        this.brokenhive1 = hive;
    }
    public Bitmap getBrokenhive1()
    {
        return this.brokenhive1;
    }
    public void setBrokenhive2(Bitmap hive)
    {
        this.brokenhive2 = hive;
    }
    public Bitmap getBrokenhive2()
    {
        return this.brokenhive2;
    }
    public void setBrokenhive3(Bitmap hive)
    {
        this.brokenhive3 = hive;
    }
    public Bitmap getBrokenhive3()
    {
        return this.brokenhive3;
    }
    public void setHive(Bitmap hive)
    {
        this.hive = hive;
    }
    public Bitmap getHive()
    {
        return this.hive;
    }
    public void setHiveSpawnTime()
    {
        this.hiveSpawnTime   = rand.nextInt(this.hiveMaxSpawn - this.hiveMinSpawn) + this.hiveMinSpawn;
    }
    public int getHiveSpawnTime()
    {
        return this.hiveSpawnTime;
    }
    public void setHiveEndTime(int het)
    {
        this.hiveEndTime = het;
    }
    public int getHiveEndTime()
    {
        return this.hiveEndTime;
    }
    public void setHiveEndInterval()
    {
        this.hiveEndInterval = rand.nextInt(this.hiveMaxEnd - this.hiveMinEnd) + this.hiveMinEnd;
    }
    public int getHiveEndInterval()
    {
        return this.hiveEndInterval;
    }
    public void setLastHiveTime(int lht)
    {
        this.lastHiveTime = lht;
    }
    public int getLastHiveTime()
    {
        return this.lastHiveTime;
    }
    public void setShowHive(boolean sh)
    {
        this.showHive = sh;
    }
    public boolean showHive()
    {
        return this.showHive;
    }
    public void setShowBrokenHive(boolean sbh)
    {
        this.showBrokenHive = sbh;
    }
    public boolean showBrokenHive()
    {
        return this.showBrokenHive;
    }
    public void setBrokenHiveTime(int bht)
    {
        this.brokenHiveTime = bht;
    }
    public int getBrokenHiveTime()
    {
        return this.brokenHiveTime;
    }
    public void setHiveX(int hx)
    {
        this.hiveX = hx;
    }
    public int getHiveX()
    {
        return this.hiveX;
    }
    public void setHiveY(int hy)
    {
        this.hiveY = hy;
    }
    public int getHiveY()
    {
        return this.hiveY;
    }
    public void setBrokenHiveX(int bhx)
    {
        this.brokenHiveX = bhx;
    }
    public int getBrokenHiveX()
    {
        return this.brokenHiveX;
    }
    public void setBrokenHiveY(int bhy)
    {
        this.brokenHiveY = bhy;
    }
    public int getBrokenHiveY()
    {
        return this.brokenHiveY;
    }
    public int getMaxHiveWasp()
    {
        return this.maxHiveWasp;
    }
    public int getMinHiveWasp()
    {
        return this.minHiveWasp;
    }
}

