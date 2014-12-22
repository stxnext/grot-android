package pl.stxnext.grot.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import pl.stxnext.grot.R;
import pl.stxnext.grot.controller.GameController;
import pl.stxnext.grot.fragment.GameFragment;
import pl.stxnext.grot.listener.GameStateChangedListener;
import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class MainActivity extends Activity implements GameStateChangedListener {

    private static final String GAME_FRAGMENT_TAG = "game_fragment_tag";
    private GameController gameController;
    private TextView scoreView;
    private TextView movesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gameController = new GameController(this);
        setContentView(R.layout.activity_main);
        Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabelId);
        scoreLabel.setTypeface(typefaceRegular);
        this.scoreView = (TextView) findViewById(R.id.scoreViewId);
        scoreView.setTypeface(typefaceBold);
        TextView movesLabel = (TextView) findViewById(R.id.movesLabelId);
        movesLabel.setTypeface(typefaceRegular);
        this.movesView = (TextView) findViewById(R.id.movesViewId);
        movesView.setTypeface(typefaceBold);
        addGameFragment();
    }

    private void addGameFragment() {
        GameFragment gameFragment = new GameFragment();
        getFragmentManager()
                .beginTransaction()
                .add(R.id.game_plain_container, gameFragment, GAME_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onGameStarted(GamePlainModel model) {
        scoreView.setText(String.format("%d", model.getScore()));
        movesView.setText(String.format("%d", model.getMoves()));
    }
}
