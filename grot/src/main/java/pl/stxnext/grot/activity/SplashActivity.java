package pl.stxnext.grot.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import pl.stxnext.grot.R;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView label = (TextView) findViewById(R.id.splash_label);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "Lato-Thin.ttf");
        label.setTypeface(typeface);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, GameActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }
}
