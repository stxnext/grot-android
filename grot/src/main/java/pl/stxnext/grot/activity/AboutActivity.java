package pl.stxnext.grot.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pl.stxnext.grot.BuildConfig;
import pl.stxnext.grot.R;

/**
 * Created by Tomasz Konieczny on 2015-01-09.
 */
public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView textView = (TextView) findViewById(R.id.version_name);
        textView.setText("v." + BuildConfig.VERSION_NAME);

        String firstAuthor;
        String secondAuthor;
        if (Math.random() >= 0.5) {
            firstAuthor = "Tomasz Konieczny";
            secondAuthor = "Mieszko Stelmach";
        } else {
            firstAuthor = "Mieszko Stelmach";
            secondAuthor = "Tomasz Konieczny";
        }

        TextView faLabel = (TextView) findViewById(R.id.first_author);
        TextView saLabel = (TextView) findViewById(R.id.second_author);

        faLabel.setText(firstAuthor);
        saLabel.setText(secondAuthor);

        findViewById(R.id.stx_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.stx_url)));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
