package pl.stxnext.grot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pl.stxnext.grot.R;

/**
 * Created by Tomasz Konieczny on 2015-01-07.
 */
public class MenuActivity extends Activity {

    public static final String START_SCREEN_ARG = "is_start_screen";

    public static final int RESTART_GAME = 1421;

    public boolean isStartScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        isStartScreen = getIntent().getBooleanExtra(START_SCREEN_ARG, false);

        View playButton = findViewById(R.id.play_game_button);
        View restartButton = findViewById(R.id.restart_button);
        if (isStartScreen) {
            findViewById(R.id.start_background).setVisibility(View.VISIBLE);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isStartScreen) {
                        Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                }
            });
        } else {
            playButton.setVisibility(View.GONE);
            restartButton.setVisibility(View.VISIBLE);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESTART_GAME);
                    finish();
                }
            });
        }


        findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.game_center_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, GameServicesActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.about_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

    }
}
