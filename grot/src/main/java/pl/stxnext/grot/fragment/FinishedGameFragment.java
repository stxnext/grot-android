package pl.stxnext.grot.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;
import pl.stxnext.grot.listener.GameStateChangedListener;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class FinishedGameFragment extends Fragment {

    private ImageButton restartButton;
    private GameStateChangedListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finished_game, container, false);
        this.restartButton = (ImageButton) view.findViewById(R.id.restart_btn);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRestartGame();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (GameStateChangedListener) getActivity();
        } catch (ClassCastException ex) {
            Log.e(AppConfig.DEBUG_TAG, "Error: Activity must implement GameStateChangedListener!");
            Log.w(AppConfig.DEBUG_TAG, "Class: FinishedGameFragment; method: onAttach() - fragment listener class cast exception");
            Log.w(AppConfig.DEBUG_TAG, ex.getMessage());
        }
    }
}
