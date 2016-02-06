package uis.rhans3.gameproject.waspwar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	GameView gv;
    Bitmap grass;
    Rect grassRect;
    SoundPool soundPool;
    TextView score1, score2, score3, score4, score5;

    Bob bob                         = new Bob();
    Wasp wasp                       = new Wasp();
    Hive hive                       = new Hive();
    int myTime                      = 0;
    int mySound                     = -1;
    Random rand                     = new Random();
    float touchX                    = 0;
    float touchY                    = 0;
    Paint drawPaint                 = new Paint();
    Paint rectPaint                 = new Paint();
    String filename                 = ".waspwar";
    BugBomb bugBomb                 = new BugBomb();
    WeedWacker weedw                = new WeedWacker();
    final int DIFFICULTY            = 500;
    final int backGroundColor       = Color.GREEN;
    public static int[] highscores  = new int[] {0, 0, 0, 0, 0};
    Dialog scoresDialog;

    Bitmap mute, unmute;
    Boolean muted;
    int muteX;
    int muteY;

	//what runs when the app instance is created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        gv = new GameView(this);
        this.setContentView(gv);

        bob.setBob(BitmapFactory.decodeResource(getResources(), R.drawable.bob));
        bob.setBobOriginal(BitmapFactory.decodeResource(getResources(), R.drawable.bob));
        bob.setBobWW(BitmapFactory.decodeResource(getResources(), R.drawable.bobweed));
        wasp.setWasp(BitmapFactory.decodeResource(getResources(), R.drawable.wasp));
        wasp.addWasp(wasp.getDefaultWaspWidth(), wasp.getDefaultWaspHeight());
        hive.setHive(BitmapFactory.decodeResource(getResources(), R.drawable.hive));
        hive.setBrokenhive1(BitmapFactory.decodeResource(getResources(), R.drawable.brokenhive1));
        hive.setBrokenhive2(BitmapFactory.decodeResource(getResources(), R.drawable.brokenhive2));
        hive.setBrokenhive3(BitmapFactory.decodeResource(getResources(), R.drawable.brokenhive3));
        weedw.setWeedWacker(BitmapFactory.decodeResource(getResources(), R.drawable.weedwacker));
        weedw.setWWSpawnTime();
        weedw.setWWEndInterval();
        bugBomb.setBugBomb(BitmapFactory.decodeResource(getResources(), R.drawable.bugbomb));
        bugBomb.setSmoke(BitmapFactory.decodeResource(getResources(), R.drawable.smoke));
        bugBomb.setSmoke2(BitmapFactory.decodeResource(getResources(), R.drawable.smoke2));
        bugBomb.setBombSpawnTime();
        bugBomb.setBombEndInterval();

        mute = BitmapFactory.decodeResource(getResources(), R.drawable.mute);
        unmute = BitmapFactory.decodeResource(getResources(), R.drawable.unmute);
        muted = false;

		grass  = BitmapFactory.decodeResource(getResources(), R.drawable.grass);
        score1 = (TextView) this.findViewById(R.id.textView1);
        score2 = (TextView) this.findViewById(R.id.textView2);
        score3 = (TextView) this.findViewById(R.id.textView3);
        score4 = (TextView) this.findViewById(R.id.textView4);
        score5 = (TextView) this.findViewById(R.id.textView5);

        scoresDialog = new Dialog(MainActivity.this);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        AssetManager assetManager = getAssets();
        try {
            AssetFileDescriptor descriptor = assetManager.openFd("stung.wav");
            AssetFileDescriptor bombExplode = assetManager.openFd("bugbombexplode.wav");
            AssetFileDescriptor hiveBreak = assetManager.openFd("hivebreak.wav");
            AssetFileDescriptor hiveBreakInitial = assetManager.openFd("hivebreakinitial.wav");
            mySound = soundPool.load(descriptor, 1);
            bugBomb.setBugBombExplode(soundPool.load(bombExplode, 1));
            hive.hiveBreaking = soundPool.load(hiveBreak, 1);
            hive.hiveBreakingInitial = soundPool.load(hiveBreakInitial, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    void init()
    {
        Canvas c = new Canvas(grass);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        grassRect = new Rect(0, 0, dm.widthPixels, dm.heightPixels);
        c.drawRect(grassRect, rectPaint);
    }
	//this will load high scores from a file
	public void load(){
		File myFile = getFileStreamPath(filename);
		if (myFile.exists()){
			try {
				BufferedReader in = new BufferedReader(new FileReader(myFile));
				
				for (int i = 0; i < 5; i++){
					highscores[i] = Integer.parseInt(in.readLine());
				}
				in.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	//this will load 
	public void save() {
		//Log.e("WaspWar", "Save1");
		FileOutputStream outputStream;
		try {
			outputStream = openFileOutput(filename,Context.MODE_PRIVATE);
			for (int i=0; i < 5; i++){
				String temp = Integer.toString(highscores[i]) + "\n";
				outputStream.write(temp.getBytes());
			}
			outputStream.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addScore(int score) {
		for (int i = 0; i < 5; i++){
			if(highscores[i] < (score/10)) {
				for (int j = 4; j > i; j--)
					highscores[j] = highscores[j - 1];
				highscores[i] = score/10;
				break;
			}
		}
	}

	//what happens when the game pauses.
	@Override
	protected void onPause(){
		super.onPause();
		gv.pause();
	}

	//what happens when the game resumes
	@Override
	protected void onResume(){
		super.onResume();
		gv.resume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//this section actually creates the game.
	public class GameView extends SurfaceView implements Runnable{
	    SurfaceHolder holder;
        Thread ViewThread = null;
        boolean threadOK  = true;
		
		public GameView(Context context) {
			super(context);
			holder = this.getHolder();
		}

		//this happens when the game is running
		@Override
		public void run() {		
			while (threadOK){
				if(!holder.getSurface().isValid()){
					continue;
				}
                //put rectangles around the bob icon for collision
                bob.setBobRect();

				//This line adds a new wasp and wasp rectangle every few seconds
				if(myTime % DIFFICULTY == 0){
		               wasp.addWasp(wasp.getDefaultWaspWidth(), wasp.getDefaultWaspHeight());
				}
				  
				// Spawn a Hive if the appropriate amount of time has passed.
				if (((myTime/100) > (hive.getLastHiveTime() + hive.getHiveSpawnTime())) && !hive.showHive())
				{
					hive.setHiveEndTime((myTime/100) + hive.getHiveEndInterval());
					hive.setShowHive(true);
				}
				
				if (hive.showHive())
				{
					if ((myTime/100) >  hive.getHiveEndTime())
					{
                        // The hive has shown long enough. Make it disappear.
						hive.nullifyHiveRect();
						hive.setShowHive(false);
						hive.setLastHiveTime(myTime/100); // The last time the hive was shown is now
						hive.setHiveSpawnTime(); // Set a random time interval to show the hive again
						hive.setHiveEndInterval(); // Set a random end time for the next time a hive shows
					}
				}
				
				// Spawn a Bomb if the appropriate amount of time has passed.
				if (((myTime/100) > (bugBomb.getLastBombTime() + bugBomb.getBombSpawnTime())) && !bugBomb.showBomb())
				{
					bugBomb.setBombEndTime((myTime / 100) + bugBomb.getBombEndInterval());
					bugBomb.setShowBomb(true);
				}
				
				if (bugBomb.showBomb())
				{
					if ((myTime/100) >  bugBomb.getBombEndTime())
					{
                        // The bomb has shown long enough. Make it disappear
						bugBomb.nullifyBugBombRect();
						bugBomb.setShowBomb(false);
						bugBomb.setLastBombTime(myTime/100); // The last time the bomb was shown is now
						bugBomb.setBombSpawnTime(); // Set a random time interval to show the bomb again.
						bugBomb.setBombEndInterval(); // Set a random end time for the next time a bomb shows
					}
				}
				
				if (bugBomb.showSmoke())
				{
					if (bugBomb.getSmokeTime() > 30)
					{
						bugBomb.nullifySmokeRect();
						bugBomb.setShowSmoke(false);
						bugBomb.setSmokeTime(0);
					}else{
						bugBomb.setSmokeTime(bugBomb.getSmokeTime() + 1);
						if(bugBomb.getSmokeAnimateTime() >= 10)
						{
							bugBomb.setSmokeAnimateTime(0);
						}else{
                            bugBomb.setSmokeAnimateTime(bugBomb.getSmokeAnimateTime() + 1);
						}
					}
				}
				
				if (hive.showBrokenHive())
				{
					if (hive.getBrokenHiveTime() > 45)
					{
						hive.nullifyBrokenHiveRect();
						hive.setShowBrokenHive(false);
						hive.setBrokenHiveTime(0);
					}else{
						hive.setBrokenHiveTime(hive.getBrokenHiveTime() + 1);
					}
				}

				// Spawn a WeedWacker
				if (((myTime/100) > (weedw.getLastWWTime() + weedw.getWWSpawnTime())) && !weedw.showWeedWacker() && !bob.isBobHoldingWW())
				{					
					weedw.setWWEndTime((myTime / 100) + weedw.getWWEndInterval()); // Set an end time for showing the weed wacker
					weedw.setShowWeedWacker(true);
				}
				
				if (weedw.showWeedWacker())
				{
					if ((myTime/100) >  weedw.getWWEndTime())
					{
                        // We've shown the weed wacker long enough.  Make it disappear.
						weedw.nullifyWWRect();
						weedw.setShowWeedWacker(false);
						weedw.setLastWWTime(myTime / 100);
						weedw.setWWSpawnTime();
						weedw.setWWEndInterval();
					}
				}
				
				if (bob.isBobHoldingWW())
				{
					if ((myTime/100) >  bob.getBobWWEndTime())
					{
						bob.setBobHoldWW(false);
						bob.setBob(bob.getBobOriginal());
						// We can now reset timer to spawn a weedwacker
						weedw.setLastWWTime(myTime / 100);
						weedw.setWWSpawnTime();
						weedw.setWWEndInterval();
					}
				}
				  
				Canvas gameCanvas = holder.lockCanvas();

				//refresh position of the rectangle so it follows the wasp.
				for(int i = 0; i < wasp.getWaspListSize(); i++){
                    Wasp waspFromList = wasp.getWaspFromList(i);
					wasp.getWaspRectFromList(i).set(waspFromList.x, waspFromList.y,
			       				waspFromList.x + (wasp.getWasp().getWidth()-20),
			       				waspFromList.y + (wasp.getWasp().getHeight()-10));
				}
				onDraw(gameCanvas);
				holder.unlockCanvasAndPost(gameCanvas);
				myTime++;
			}
		}
		
		//this is what happens when the canvas is drawn
		protected void onDraw(Canvas canvas){
			drawPaint.setAlpha(255);
			canvas.drawColor(backGroundColor);
            muteX = (canvas.getWidth() - mute.getWidth()) - 20;
            muteY = (canvas.getHeight()- canvas.getHeight()) + 20;
            //canvas.drawBitmap(grass, 0, 0, drawPaint);

			//Delete comments here to see the rectangles visually for verification
			//rectPaint.setAlpha(255);
			//rectPaint.setColor(Color.WHITE);
			//canvas.drawRect(bob.bobRect, rectPaint);
			
			canvas.drawBitmap(bob.getBob(), bob.getBobX(), bob.getBobY(), drawPaint);

            if (!muted){
                canvas.drawBitmap(unmute, muteX, muteY, drawPaint);
            }
            else
            {
                canvas.drawBitmap(mute, muteX, muteY, drawPaint);
            }

			if (bugBomb.showBomb())
			{
				if(bugBomb.getBugBombRect() == null)
				{
                    bugBomb.setBugBombX(rand.nextInt(canvas.getWidth()));
                    bugBomb.setBugBombY(rand.nextInt(canvas.getHeight()));
					bugBomb.setBugBombRect();
				}
				canvas.drawBitmap(bugBomb.getBugBomb(), bugBomb.getBugBombX(), bugBomb.getBugBombY(), drawPaint);
			}
			if (hive.showHive())
			{
				if (hive.getHiveRect() == null)
				{
					hive.setHiveX(rand.nextInt(canvas.getWidth()));
					hive.setHiveY(rand.nextInt(canvas.getHeight()));
					hive.setHiveRect();
				}
				canvas.drawBitmap(hive.getHive(), hive.getHiveX(), hive.getHiveY(), drawPaint);
			}
			
			if (bugBomb.showSmoke())
			{
				if (bugBomb.getSmokeRect() == null)
				{
                    Bitmap smoke = bugBomb.getSmoke();
                    bugBomb.setSmokeX(bugBomb.getBugBombX() - (smoke.getWidth()/2));
					bugBomb.setSmokeY(bugBomb.getBugBombY() - (smoke.getHeight()/2));
					bugBomb.setSmokeRect();
				}
				if (bugBomb.getSmokeAnimateTime() <= 5)
				{	
					canvas.drawBitmap(bugBomb.getSmoke(), bugBomb.getSmokeX(), bugBomb.getSmokeY(), drawPaint);
				}else{
					canvas.drawBitmap(bugBomb.getSmoke2(), bugBomb.getSmokeX(), bugBomb.getSmokeY(), drawPaint);
				}
			}
						
			if (hive.showBrokenHive())
			{
				if (hive.getBrokenHiveTime() <= 15)
				{
					hive.setBrokenHiveX(hive.getHiveX() - (hive.getBrokenhive1().getWidth() / 2));
					hive.setBrokenHiveY(hive.getHiveY() - (hive.getBrokenhive1().getHeight() - hive.getBrokenhive1().getHeight()));
					canvas.drawBitmap(hive.getBrokenhive1(), hive.getBrokenHiveX(), hive.getBrokenHiveY(), drawPaint);
				}else if (hive.getBrokenHiveTime() <= 30)
				{
					hive.setBrokenHiveX(hive.getHiveX() - (hive.getBrokenhive2().getWidth()/2));
					hive.setBrokenHiveY(hive.getHiveY() - (hive.getBrokenhive2().getHeight() - hive.getBrokenhive2().getHeight()));
					canvas.drawBitmap(hive.getBrokenhive2(), hive.getBrokenHiveX(), hive.getBrokenHiveY(), drawPaint);
				}else{
					hive.setBrokenHiveX(hive.getHiveX() - (hive.getBrokenhive3().getWidth()/2));
					hive.setBrokenHiveY(hive.getHiveY() - (hive.getBrokenhive3().getHeight() - hive.getBrokenhive3().getHeight()));
					canvas.drawBitmap(hive.getBrokenhive3(), hive.getBrokenHiveX(), hive.getBrokenHiveY(), drawPaint);
				}
			}
			
			if (weedw.showWeedWacker())
			{
				if (weedw.getWWRect() == null) {
                    weedw.setWeedWackerX(rand.nextInt(canvas.getWidth()));
					weedw.setWeedWackerY(rand.nextInt(canvas.getHeight()));
                    weedw.setWWRect();
				}
				canvas.drawBitmap(weedw.getWeedWacker(), weedw.getWeedWackerX(), weedw.getWeedWackerY(), drawPaint);
			}
			
			for(int i = 0; i < wasp.getWaspListSize(); i++){
				//this gets the wasps individual speed and location behavior
				wasp.getWaspFromList(i).update(canvas.getWidth(), canvas.getHeight());
				//uncomment next line to visually see rectangles around wasps.
				//canvas.drawRect(waspRects.get(i), rectPaint);

				canvas.drawBitmap(wasp.getWasp(), wasp.getWaspFromList(i).x, wasp.getWaspFromList(i).y, drawPaint);
				
				//this is what occurs if the bob rectangle collides with a wasp rectangle.
				if(Rect.intersects(bob.getBobRect(), wasp.getWaspRectFromList(i))){
					if (bob.isBobHoldingWW())
                    {
                        wasp.removeWaspFromList(i);
					}else{
						wasp.clearWaspList();
						load();
						addScore(myTime);
						save();
                        if (!muted) {
                            soundPool.play(mySound, 1, 1, 0, 0, 1);
                        }
						showScores(gv);
						break;
					}
				}
			}
			if(bugBomb.showSmoke())
            {
                for(int i = 0; i < wasp.getWaspListSize(); i++){

                    if(Rect.intersects(bugBomb.getSmokeRect(), wasp.getWaspRectFromList(i))){
                        wasp.removeWaspFromList(i);
                    }
                }
            }
			if(hive.showHive() && Rect.intersects(hive.getHiveRect(), bob.getBobRect()))
			{
                // Add a random number of new wasps to the game

                if (!muted) {
                    soundPool.play(hive.hiveBreakingInitial, 1, 1, 0, 0, 2);
                    soundPool.play(hive.hiveBreaking, 1, 1, 0, 0, 2);
                }
                hive.setShowBrokenHive(true);
				if (hive.showHive())
					wasp.addMultipleWasps(rand.nextInt(hive.getMaxHiveWasp() - hive.getMinHiveWasp()) + hive.getMinHiveWasp());
				hive.setShowHive(false);
				hive.nullifyHiveRect();
				hive.setLastHiveTime(myTime/100);
				hive.setHiveSpawnTime();
			}
			if(bugBomb.showBomb() && Rect.intersects(bugBomb.getBugBombRect(), bob.getBobRect()))
			{
				// Bob has set off the BugBomb.
                if(!muted) {
                    soundPool.play(bugBomb.getBugBombExplode(), 1, 1, 0, 0, 2);
                }
                bugBomb.setShowSmoke(true);
				bugBomb.setShowBomb(false);
				bugBomb.nullifyBugBombRect();
				bugBomb.setLastBombTime(myTime / 100);
				bugBomb.setBombSpawnTime();
			}
			
			if(weedw.showWeedWacker() && Rect.intersects(weedw.getWWRect(), bob.getBobRect()))
			{
                // Bob has picked up the weed wacker
                if (weedw.showWeedWacker())
				{
					bob.setBob(bob.getBobWW());  // Show Bob as holding the weed wacker
                    bob.setBobHoldWW(true);
					bob.setBobWWEndTime(myTime); // Bob can only hold the wacker for a certain time
				}
				weedw.setShowWeedWacker(false);
				weedw.nullifyWWRect();
			}
		}
		
		public void showScores(View view){
			runOnUiThread(new Runnable() {
		public void run() {
            pause();
			scoresDialog.setContentView(R.layout.high_scores);
			final TextView scoreTitle = (TextView) scoresDialog.findViewById(R.id.textView6);
			final TextView score1 = (TextView) scoresDialog.findViewById(R.id.textView1);
			final TextView score2 = (TextView) scoresDialog.findViewById(R.id.textView2);
			final TextView score3 = (TextView) scoresDialog.findViewById(R.id.textView3);
			final TextView score4 = (TextView) scoresDialog.findViewById(R.id.textView4);
			final TextView score5 = (TextView) scoresDialog.findViewById(R.id.textView5);
			final Button quit = (Button) scoresDialog.findViewById(R.id.quit);
			final Button replay = (Button) scoresDialog.findViewById(R.id.play1);
			
			quit.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View v) {
                // this will quit the app through our Landing Page class via onActivityResult()
                Intent intent = getIntent();
                intent.putExtra("exit", 1);
                setResult(RESULT_OK, intent);
                finish();
				}
				
			});
			
			replay.setOnClickListener(new Button.OnClickListener(){

				@Override
				public void onClick(View v) {
					bob = new Bob();
                    bob.setBob(BitmapFactory.decodeResource(getResources(), R.drawable.bob));
                    bob.setBobOriginal(BitmapFactory.decodeResource(getResources(), R.drawable.bob));
                    bob.setBobWW(BitmapFactory.decodeResource(getResources(), R.drawable.bobweed));
                    bob.setBobRect();
                    touchX = 0;
					touchY = 0;
                    myTime = 0;

					hive.setShowHive(false);
                    hive.setShowBrokenHive(false);
                    hive.setLastHiveTime(0);
                    hive.nullifyHiveRect();
                    hive.nullifyBrokenHiveRect();

                    bugBomb.setShowSmoke(false);
                    bugBomb.nullifySmokeRect();
                    bugBomb.nullifyBugBombRect();
                    bugBomb.setShowBomb(false);
                    bugBomb.setLastBombTime(0);

                    weedw.nullifyWWRect();
                    weedw.setLastWWTime(0);

                    wasp.clearWaspList();
                    wasp.addWasp(wasp.getDefaultWaspWidth(), wasp.getDefaultWaspHeight());

					scoresDialog.dismiss();
					resume();
				}
				
			});
			scoreTitle.setText("OUCH!  You've been stung!  Your final score was " + myTime / 10);
			score1.setText("1. " + Integer.toString(highscores[0]));
			score2.setText("2. " + Integer.toString(highscores[1]));
			score3.setText("3. " + Integer.toString(highscores[2]));
			score4.setText("4. " + Integer.toString(highscores[3]));
			score5.setText("5. " + Integer.toString(highscores[4]));

            //********************************************************************************//
            // Prevent the user from clicking outside the score dialog and seeing a paused
            // game and not being able to do anything else.  Effectively, the game would
            // appear frozen.
            //********************************************************************************//
			scoresDialog.setCanceledOnTouchOutside(false);
			scoresDialog.show();
				  }
			});
		}
			
		
		
		public void pause() {
			threadOK = false;
			while(true){
				try {
					if (ViewThread != null)
						ViewThread.join();
				} catch (InterruptedException e){
					e.printStackTrace();
				}
				break;
			}
			ViewThread = null;
		}
		
		public void resume() {
			threadOK = true;
			ViewThread = new Thread(this);
			ViewThread.start();
		}
		
		//This method has a switch statement for finger actions on the bob.  What happens when you
		//touch the bob, move the bob, or lift up your finger.
		//@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent event){
			switch (event.getAction()){
			case MotionEvent.ACTION_DOWN :
			{
                touchX = event.getRawX();
				touchY = event.getRawY() - 200;
				if(touchX > bob.getBobX()
					&& touchX < bob.getBobX() + bob.getBob().getWidth()
					&& touchY > bob.getBobY()
					&& touchY < bob.getBobY() + bob.getBob().getHeight()){
					bob.setBobSelected(true);
				}
                else if (touchX > muteX && touchY > muteY)
                {
                    if (muted)
                    {
                        // unmute the sounds
                        muted = false;
                    }
                    else
                    {
                        // mute the sounds
                        muted = true;
                    }
                }
			}
			break;
			case MotionEvent.ACTION_MOVE :
			{
				touchX = event.getRawX() - (bob.getBob().getWidth()/2);
				touchY = (event.getRawY() - 200) - (bob.getBob().getHeight()/2);
				
				if(bob.isBobSelected()){
					bob.setBobX((int) touchX);
					bob.setBobY((int) touchY);
				}
			}
			break;
			case MotionEvent.ACTION_UP :
			{
				bob.setBobSelected(false);
			}
			break;
			} // end of switch
			
			return true;
		}
	}
}
