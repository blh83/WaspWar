package ryanbrandongames.waspwars;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;



public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static int GRASS_ROWS = 6;
    private static int GRASS_COLUMNS = 4;
    private static int GRASS_UPDATE_INTERVAL = 1;
    private static int GRASS_REGROW_INTERVAL = 10;
    private static int REQUEST_LEADERBOARD = 88658030;
    private GoogleApiClient mGoogleApiClient;

	GameView gv;
    SoundPool soundPool;
    TextView score1, score2, score3, score4, score5;
    private Tracker mTracker;
    DisplayMetrics metrics;
    int windowWidth;
    int windowHeight;
    int cellWidth, cellHeight;
    int numColumns, numRows;
    private int[][] grassGrowLevel;
    private Rect[][] grassRects;
    private Bitmap[][] grassBitmaps;
    private int[][] grassLastUpdateTime;

    Bob bob                         = new Bob();
    Wasp wasp                       = new Wasp();
    Hive hive                       = new Hive();
    int myTime                      = 0;
    int mySound                     = -1;
    Random rand                     = new Random();
    float touchX                    = 0;
    float touchY                    = 0;
    Paint drawPaint                 = new Paint();
    String filename                 = ".waspwars";
    BugBomb bugBomb                 = new BugBomb();
    WeedWacker weedw                = new WeedWacker();
    final int DIFFICULTY            = 500;
    int lastGrassGrowTime           = myTime;
    public static int[] highscores  = new int[] {0, 0, 0, 0, 0};
    Dialog scoresDialog;

    Bitmap pause;
    Boolean muted, paused;
    int pauseX;
    int pauseY;

	//what runs when the app instance is created
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        gv = new GameView(this);
        this.setContentView(gv);
        WaspWars analytics = (WaspWars)getApplication();
        mTracker = analytics.getDefaultTracker();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

		getActionBar().hide();

        bob.setBob(BitmapFactory.decodeResource(getResources(), R.drawable.bobfront));
        bob.setBobOriginal(BitmapFactory.decodeResource(getResources(), R.drawable.bobfront));
        bob.setBobWW(BitmapFactory.decodeResource(getResources(), R.drawable.bobfrontweed));
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
        bugBomb.setSmoke(BitmapFactory.decodeResource(getResources(), R.drawable.bombsmoke));
        bugBomb.setSmoke2(BitmapFactory.decodeResource(getResources(), R.drawable.bombsmoke2));
        bugBomb.setBombSpawnTime();
        bugBomb.setBombEndInterval();

        pause = BitmapFactory.decodeResource(getResources(), R.drawable.pause);
        muted = false;
        paused = false;

        score1 = (TextView) this.findViewById(R.id.textView1);
        score2 = (TextView) this.findViewById(R.id.textView2);
        score3 = (TextView) this.findViewById(R.id.textView3);
        score4 = (TextView) this.findViewById(R.id.textView4);
        score5 = (TextView) this.findViewById(R.id.textView5);

        // Grass initialization stuff
        numColumns = 4;
        numRows = 6;
        metrics = getApplicationContext().getResources().getDisplayMetrics();
        windowWidth = metrics.widthPixels;
        windowHeight = metrics.heightPixels;
        cellWidth = windowWidth / GRASS_COLUMNS;
        cellHeight = windowHeight / GRASS_ROWS;
        grassGrowLevel = new int[numColumns][numRows];
        grassRects = new Rect[numColumns][numRows];
        grassBitmaps = new Bitmap[numColumns][numRows];
        grassLastUpdateTime = new int[numColumns][numRows];

        for (int c = 0; c < numColumns; c++) {
            for (int r = 0; r < numRows; r++) {
                grassGrowLevel[c][r] = 3;
                grassRects[c][r] = new Rect(c * cellWidth, r * cellHeight,
                        (c + 1) * cellWidth, (r + 1) * cellHeight);
                grassBitmaps[c][r] = BitmapFactory.decodeResource(getResources(), R.drawable.grassgrow3);
                grassLastUpdateTime[c][r] = myTime;
            }
        }

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

	@Override
	protected void onStart() {
		super.onStart();
		//Log.d(TAG, "onStart(): connecting");
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//Log.d(TAG, "onStop(): disconnecting");
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
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
                        Long duration = (long)((myTime/100) - weedw.getLastWWTime());
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Timing")
                                .setAction("WeedWackerDuration")
                                .setValue(duration)
                                .build());
						weedw.setLastWWTime(myTime / 100);
						weedw.setWWSpawnTime();
						weedw.setWWEndInterval();
					}
				}

                if ((myTime/100) > (lastGrassGrowTime + GRASS_REGROW_INTERVAL)) {
                    growTheGrass();
                    lastGrassGrowTime = myTime/100;
                }

				Canvas gameCanvas = holder.lockCanvas();

				//refresh position of the rectangle so it follows the wasp.
				for(int i = 0; i < wasp.getWaspListSize(); i++){
                    Wasp waspFromList = wasp.getWaspFromList(i);
					wasp.getWaspRectFromList(i).set(waspFromList.x, waspFromList.y,
			       				waspFromList.x + (wasp.getWasp().getWidth()-20),
			       				waspFromList.y + (wasp.getWasp().getHeight()-10));
				}
				canvasDraw(gameCanvas);
				holder.unlockCanvasAndPost(gameCanvas);
				myTime++;
			}
		}

		//this is what happens when the canvas is drawn
		protected void canvasDraw(Canvas canvas){
			drawPaint.setAlpha(255);
            pauseX = (windowWidth - pause.getWidth()) - 20;
            pauseY = 20;
            Paint drawPaint = new Paint();

            // Draw our grass grid
            for (int c = 0; c < numColumns; c++) {
                for (int r = 0; r < numRows; r++) {
                    canvas.drawBitmap(grassBitmaps[c][r], null, grassRects[c][r], drawPaint);
                }
            }

			canvas.drawBitmap(bob.getBob(), bob.getBobX(), bob.getBobY(), drawPaint);
            canvas.drawBitmap(pause, pauseX, pauseY, drawPaint);

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

            // Cut the grass for the square Bob is in, if appropriate
            for (int c = 0; c < numColumns; c++) {
                for (int r = 0; r < numRows; r++) {
                    if (Rect.intersects(bob.getBobRect(), grassRects[c][r])) {
                        if ((myTime/ 100) > grassLastUpdateTime[c][r] + GRASS_UPDATE_INTERVAL) {
                            grassLastUpdateTime[c][r] = myTime/100;
                            switch (grassGrowLevel[c][r]) {
                                case 3:
                                    grassBitmaps[c][r] = BitmapFactory.decodeResource(getResources(), R.drawable.grassgrow2);
                                    grassGrowLevel[c][r] = 2;
                                    break;
                                case 2:
                                    grassBitmaps[c][r] = BitmapFactory.decodeResource(getResources(), R.drawable.grassgrow1);
                                    grassGrowLevel[c][r] = 1;
                                    break;
                                case 1:
                                    grassBitmaps[c][r] = BitmapFactory.decodeResource(getResources(), R.drawable.grasscut);
                                    grassGrowLevel[c][r] = 0;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
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
						showScores();
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
                if (!muted) {
                    soundPool.play(hive.hiveBreakingInitial, 1, 1, 0, 0, 2);
                    soundPool.play(hive.hiveBreaking, 1, 1, 0, 0, 2);
                }
                hive.setShowBrokenHive(true);
                // Add a random number of new wasps to the game
				if (hive.showHive())
					wasp.addMultipleWasps(rand.nextInt(hive.getMaxHiveWasp() - hive.getMinHiveWasp()) + hive.getMinHiveWasp());
				hive.setShowHive(false);
				hive.nullifyHiveRect();
				hive.setLastHiveTime(myTime / 100);
				hive.setHiveSpawnTime();
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("BobAction")
                        .setAction("HiveBroken")
                        .build());
			}

			if(bugBomb.showBomb() && Rect.intersects(bugBomb.getBugBombRect(), bob.getBobRect()))
			{
                if(!muted) {
                    soundPool.play(bugBomb.getBugBombExplode(), 1, 1, 0, 0, 2);
                }
                // Bob has set off the BugBomb.
                bugBomb.setShowSmoke(true);
				bugBomb.setShowBomb(false);
				bugBomb.nullifyBugBombRect();
				bugBomb.setLastBombTime(myTime / 100);
				bugBomb.setBombSpawnTime();
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("BobAction")
                        .setAction("BugBomb")
                        .build());
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

        // Function that is called on an interval to re-grow cut grass
        private void growTheGrass(){
            for (int c = 0; c < numColumns; c++) {
                for (int r = 0; r < numRows; r++) {
                    switch (grassGrowLevel[c][r]) {
                        case 0:
                            grassBitmaps[c][r] = BitmapFactory.decodeResource(getResources(), R.drawable.grassgrow1);
                            grassGrowLevel[c][r] = 1;
                            break;
                        case 1:
                            grassBitmaps[c][r] = BitmapFactory.decodeResource(getResources(), R.drawable.grassgrow2);
                            grassGrowLevel[c][r] = 2;
                            break;
                        case 2:
                            grassBitmaps[c][r] = BitmapFactory.decodeResource(getResources(), R.drawable.grassgrow3);
                            grassGrowLevel[c][r] = 3;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

		public void showScores(){
        	runOnUiThread(new Runnable() {
            public void run() {
                pause();
                WaspWarCustomDialog.OnDialogClickListener dialogListener = new WaspWarCustomDialog.OnDialogClickListener() {
                    @Override
                    public void playerRestarted() {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("ReStart")
                                .build());
                        bob = new Bob();
                        bob.setBob(BitmapFactory.decodeResource(getResources(), R.drawable.bobfront));
                        bob.setBobOriginal(BitmapFactory.decodeResource(getResources(), R.drawable.bobfront));
                        bob.setBobWW(BitmapFactory.decodeResource(getResources(), R.drawable.bobfrontweed));
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

                        // Start the grass as fully grown
                        for (int c = 0; c < numColumns; c++) {
                            for (int r = 0; r < numRows; r++) {
                                // No need to reset the grass Rects since they won't change.
                                grassGrowLevel[c][r] = 3;
                                grassBitmaps[c][r] = BitmapFactory.decodeResource(getResources(), R.drawable.grassgrow3);
                                grassLastUpdateTime[c][r] = myTime;
                            }
                        }

                        paused = false;
                        scoresDialog.dismiss();
                        resume();
                    }

                    @Override
                    public void playerResumed() {
                        paused = false;
                        resume();
                    }

                    @Override
                    public void playerQuit() {
                        mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Quit")
                                .build());
                        paused = false;
                        // this will quit the app through our Landing Page class via onActivityResult()
                        Intent intent = getIntent();
                        intent.putExtra("exit", 1);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void playerMuted() {
                        if (muted)
                        {
                            // unmute the sounds
                            muted = false;
                            mTracker.send(new HitBuilders.EventBuilder()
                                    .setCategory("Action")
                                    .setAction("UnMute")
                                    .build());
                        }
                        else
                        {
                            // mute the sounds
                            muted = true;
                            mTracker.send(new HitBuilders.EventBuilder()
                                .setCategory("Action")
                                .setAction("Mute")
                                .build());
                        }
                        paused = false;
                        scoresDialog.dismiss();
                        resume();
                    }
                };
                scoresDialog = new WaspWarCustomDialog(MainActivity.this, dialogListener, paused, myTime, highscores);

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
                else if (touchX > pauseX && touchY > pauseY)
                {
                    paused = true;
                   // showScores();
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient,
							getString(R.string.LEADERBOARD_ID)), REQUEST_LEADERBOARD);
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
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Player p = Games.Players.getCurrentPlayer(mGoogleApiClient);
        String displayName;
        if (p == null) {
            //Log.w(TAG, "mGamesClient.getCurrentPlayer() is NULL!");
            displayName = "???";
        } else {
            displayName = p.getDisplayName();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.d(TAG, "onConnectionSuspended(): attempting to connect");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO: Something useful
    }
}
