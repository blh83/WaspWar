package uis.rhans3.gameproject.waspwar;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.Random;

/**
 * Created by B on 8/29/2015.
 */
public class Bob {
    static final Random rand = new Random();
    private int bobX, bobY, bobWWEndTime;
    private int bobWWMaxEndTime, bobWWMinEndTime;
    private boolean bobSelected, bobHoldWW;
    private Rect bobRect;
    private Bitmap bob, bobOriginal, bobWW;

    public Bob()
    {
        this.bobX               = 500;
        this.bobY               = 500;
        this.bobWWEndTime       = 0;
        this.bobWWMaxEndTime    = 10;
        this.bobWWMinEndTime    = 5;
        this.bobSelected        = false;
        this.bobHoldWW          = false;
        this.bob                = null;
        this.bobWW              = null;
        this.bobRect            = null;
        this.bobOriginal        = null;
    }

    public void setBobRect(){
        this.bobRect = new Rect(this.bobX + 10, this.bobY, this.bobX + (this.bob.getWidth() - 10), this.bobY + (this.bob.getHeight()-40));
    }

    public Rect getBobRect(){
        return this.bobRect;
    }

    public int getBobX()
    {
        return this.bobX;
    }

    public void setBobX(int x)
    {
        this.bobX = x;
    }

    public int getBobY()
    {
        return this.bobY;
    }

    public void setBobY(int y)
    {
        this.bobY = y;
    }

    public int getBobWWEndTime()
    {
        return this.bobWWEndTime;
    }

    public void setBobWWEndTime(int myTime)
    {
        this.bobWWEndTime = (myTime/100) + (rand.nextInt(this.bobWWMaxEndTime - this.bobWWMinEndTime) + this.bobWWMinEndTime);
    }

    public boolean isBobSelected()
    {
        return this.bobSelected;
    }

    public void setBobSelected(boolean selected)
    {
        this.bobSelected = selected;
    }

    public boolean isBobHoldingWW()
    {
        return this.bobHoldWW;
    }

    public void setBobHoldWW(boolean hold)
    {
        this.bobHoldWW = hold;
    }
    public Bitmap getBob()
    {
        return bob;
    }

    public void setBob(Bitmap bob)
    {
        this.bob = bob;
    }

    public Bitmap getBobWW()
    {
        return this.bobWW;
    }

    public void setBobWW(Bitmap bobWW)
    {
        this.bobWW = bobWW;
    }

    public Bitmap getBobOriginal()
    {
        return this.bobOriginal;
    }

    public void setBobOriginal(Bitmap bobOr)
    {
        this.bobOriginal = bobOr;
    }
}
