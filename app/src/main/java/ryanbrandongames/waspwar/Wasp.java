package ryanbrandongames.waspwar;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.Random;

class  Wasp {
	static final Random rand = new Random();
	public int x , y, defaultWaspWidth, defaultWaspHeight;
	private int speedX, speedY;
    private ArrayList<Wasp> wasps;
    private ArrayList<Rect> waspRects;
    private Bitmap wasp;

	public Wasp(int width, int height){
		float entryRand = rand.nextFloat();
		if(entryRand > .25 && entryRand <= .5){
			x = 0;
			y = (int) (rand.nextFloat() * height);
		}else if (entryRand > .5 && entryRand <=.75){
			x = width;
			y = (int) (rand.nextFloat() * height);
		}else if (entryRand > .75){
			x = (int) (rand.nextFloat() * width);
			y = 0;
		}else{
			x = (int) (rand.nextFloat() * width);
			y = height;
		}
		speedX = (int) ((rand.nextFloat() + .05) * 10);
		speedY = (int) ((rand.nextFloat() + .05) * 10);
	}

    public Wasp()
    {
        this.wasp               =   null;
        this.wasps              =   new ArrayList<Wasp>();
        this.waspRects          =   new ArrayList<Rect>();
        this.defaultWaspWidth   =   1000;
        this.defaultWaspHeight  =   2000;

    }
	
	protected void update(int gvWidth, int gvHeight){
		x = x + speedX;
		y = y + speedY;
		
		if (x < 0){
			speedX = -speedX;
			x = 0;
		}
		
		if (x > gvWidth - 76){
			speedX = -speedX;
			x = gvWidth - 76;
		}
		
		if (y < 0){
			speedY = -speedY;
			y = 0;
		}
		
		if (y > gvHeight - 76){
			speedY = -speedY;
			y = gvHeight - 76;
		}
	}

    public void setWasp(Bitmap wasp)
    {
        this.wasp = wasp;
    }
    public Bitmap getWasp()
    {
        return this.wasp;
    }
    public void addWasp(int width, int height)
    {
        this.wasps.add(new Wasp(width, height));
        this.addWaspRect();
    }
    public void addMultipleWasps(int count)
    {
        for (int i = 0; i < count; i++)
        {
            this.addWasp(1000, 2000);
            this.addWaspRect();
        }
    }
    private void addWaspRect()
    {
        this.waspRects.add(new Rect(this.wasps.get(this.wasps.size()-1).x, this.wasps.get(this.wasps.size()-1).y,
                this.wasps.get(this.wasps.size()-1).x + (this.wasp.getWidth()-20),
                this.wasps.get(this.wasps.size()-1).y + (this.wasp.getHeight()-10)));
    }
    public int getWaspListSize()
    {
        return this.wasps.size();
    }
    public Wasp getWaspFromList(int idx)
    {
        return this.wasps.get(idx);
    }
    public Rect getWaspRectFromList(int idx)
    {
        return this.waspRects.get(idx);
    }
    public void removeWaspFromList(int idx)
    {
        this.wasps.remove(idx);
        this.waspRects.remove(idx);
    }
    public void clearWaspList()
    {
        this.wasps.clear();
        this.waspRects.clear();
    }
    public int getDefaultWaspWidth()
    {
        return this.defaultWaspWidth;
    }
    public int getDefaultWaspHeight()
    {
        return this.defaultWaspHeight;
    }
}
