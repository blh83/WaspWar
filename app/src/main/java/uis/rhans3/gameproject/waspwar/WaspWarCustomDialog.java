package uis.rhans3.gameproject.waspwar;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by B on 2/13/2016.
 */


public class WaspWarCustomDialog extends Dialog implements View.OnClickListener {

    public Activity activity;
    public Button playagain, resume, quit, mute;
    private boolean paused;
    private final OnDialogClickListener listener;
    private TextView scoreTitle, score1, score2, score3, score4, score5;
    private int myTime;
    private int[] highscores;

    public interface OnDialogClickListener {
        void playerRestarted();
        void playerResumed();
        void playerQuit();
        void playerMuted();
    }

    public WaspWarCustomDialog(Activity a, OnDialogClickListener listener, boolean paused, int myTime, int[] highscores) {
        super(a);
        this.activity = a;
        this.paused = paused;
        this.listener = listener;
        this.myTime = myTime;
        this.highscores = highscores;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!paused){
            // The player got stung
            setContentView(R.layout.high_scores);
            scoreTitle = (TextView) this.findViewById(R.id.textView6);
            scoreTitle.setText("OUCH!  You've been stung!  Your final score was " + myTime / 10);
            playagain = (Button) this.findViewById(R.id.playagain);
            playagain.setOnClickListener(this);
        } else {
            // The player has paused the game
            setContentView(R.layout.pause_menu);
            scoreTitle = (TextView) this.findViewById(R.id.textView6);
            scoreTitle.setText("Please choose an option below\nHigh Scores:");
            mute   = (Button) this.findViewById(R.id.mute);
            mute.setOnClickListener(this);
            resume = (Button) this.findViewById(R.id.resume);
            resume.setOnClickListener(this);
        }
        quit = (Button) this.findViewById(R.id.quit);
        quit.setOnClickListener(this);

        score1 = (TextView) this.findViewById(R.id.textView1);
        score2 = (TextView) this.findViewById(R.id.textView2);
        score3 = (TextView) this.findViewById(R.id.textView3);
        score4 = (TextView) this.findViewById(R.id.textView4);
        score5 = (TextView) this.findViewById(R.id.textView5);

        score1.setText("1. " + Integer.toString(highscores[0]));
        score2.setText("2. " + Integer.toString(highscores[1]));
        score3.setText("3. " + Integer.toString(highscores[2]));
        score4.setText("4. " + Integer.toString(highscores[3]));
        score5.setText("5. " + Integer.toString(highscores[4]));
    }

    @Override
    public void onClick(View v) {
        // Following are the onClickListeners that were registered above.  These call fxns
        // inside of MainActivity where we create a WaspWarCustomDialog.
        switch (v.getId()) {
            case R.id.resume:
                listener.playerResumed();
                break;
            case R.id.quit:
                listener.playerQuit();
                break;
            case R.id.mute:
                listener.playerMuted();
                break;
            case R.id.playagain:
                listener.playerRestarted();
                break;
        }
        dismiss();
    }
}
