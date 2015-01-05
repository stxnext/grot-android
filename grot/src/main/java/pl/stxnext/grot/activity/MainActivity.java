package pl.stxnext.grot.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.List;

import pl.stxnext.grot.R;
import pl.stxnext.grot.controller.GameController;
import pl.stxnext.grot.fragment.FinishedGameFragment;
import pl.stxnext.grot.fragment.GameFragment;
import pl.stxnext.grot.listener.GameStateChangedListener;
import pl.stxnext.grot.model.FieldTransition;
import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class MainActivity extends Activity implements GameStateChangedListener, GameController.GameControllerListener {

    private static final String GAME_FRAGMENT_TAG = "game_fragment_tag";
    private static final String FINISHED_GAME_FRAGMENT_TAG = "finished_game_fragment_tag";
    private GameController gameController;
    private TextSwitcher scoreSwitcher;
    private TextView movesView;
    private Handler handler;
    private GameFragment gameFragment;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gameController = new GameController(this);
        setContentView(R.layout.activity_main);
        Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
        final Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabelId);
        scoreLabel.setTypeface(typefaceRegular);
        this.scoreSwitcher = (TextSwitcher) findViewById(R.id.scoreViewId);
        this.scoreSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.score_text_view, null);
                TextView textView = (TextView) view.findViewById(R.id.scoreTextView);
                textView.setTypeface(typefaceBold);

                return textView;
            }
        });

        TextView movesLabel = (TextView) findViewById(R.id.movesLabelId);
        movesLabel.setTypeface(typefaceRegular);
        this.movesView = (TextView) findViewById(R.id.movesViewId);
        movesView.setTypeface(typefaceBold);
        this.handler = new Handler();
        addGameFragment();
    }

    private void addGameFragment() {
        this.gameFragment = new GameFragment();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.game_plain_container, gameFragment, GAME_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onGameStarted(final GamePlainModel model) {
        score = model.getScore();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (scoreSwitcher.getInAnimation() != null) {
                    scoreSwitcher.getInAnimation().setAnimationListener(null);
                }
                scoreSwitcher.setText(String.format("%d", score));
                movesView.setText(String.format("%d", model.getMoves()));
            }
        });
        gameController.setNewGamePlainModel(model);
    }

    @Override
    public void onFieldPressed(int position) {
        gameController.updateGameState(position);
    }

    @Override
    public void onRestartGame() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.game_plain_container, gameFragment, GAME_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onAnimationEnd(final GamePlainModel model, List<FieldTransition> fieldTransitions) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                movesView.setText(String.format("%d", model.getMoves()));
            }
        });

        updateScore(model.getScore());

        gameController.updateGamePlain(fieldTransitions);
    }

    private void updateScore(final int currentScore) {
        if (currentScore - score > 20) {
            score = score + 10;
        } else if (currentScore - score > 10) {
            score = score + 5;
        } else {
            score++;
        }

        if (score == currentScore) {
            scoreSwitcher.setInAnimation(this, R.anim.score_slide_in_slow);
            scoreSwitcher.setOutAnimation(this, R.anim.score_slide_out_slow);
        } else {
            scoreSwitcher.setInAnimation(this, R.anim.score_slide_in);
            scoreSwitcher.setOutAnimation(this, R.anim.score_slide_out);
        }

        scoreSwitcher.setText(String.format("%d", score));
        scoreSwitcher.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (score < currentScore) {
                    updateScore(currentScore);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void updateGameInfo(GamePlainModel model, List<FieldTransition> fieldTransitions) {
        gameFragment.updateGameBoard(model, fieldTransitions);
    }

    @Override
    public void onGameFinished(GamePlainModel model) {
        FinishedGameFragment finishedGameFragment = new FinishedGameFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.game_plain_container, finishedGameFragment, FINISHED_GAME_FRAGMENT_TAG)
                .commit();
    }
}
