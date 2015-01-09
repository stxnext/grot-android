package pl.stxnext.grot.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.example.games.basegameutils.BaseGameActivity;

import pl.stxnext.grot.R;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameServicesActivity extends BaseGameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_service_activity);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginUserInitiatedSignIn();
            }
        });
    }

    @Override
    public void onSignInFailed() {
        findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onSignInSucceeded() {
        Toast.makeText(this, "OK!", Toast.LENGTH_SHORT).show();
    }
}
