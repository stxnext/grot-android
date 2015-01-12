package pl.stxnext.grot.activity;

import android.app.Activity;
import android.os.Bundle;

import pl.stxnext.grot.R;

/**
 * Created by Tomasz Konieczny on 2015-01-09.
 */
public class HelpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
