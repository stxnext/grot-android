package pl.stxnext.grot.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.transition.Fade;

import pl.stxnext.grot.R;

/**
 * Created by Tomasz Konieczny on 2015-01-07.
 */
public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);
    }
}
