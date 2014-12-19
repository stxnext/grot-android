package pl.stxnext.grot.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import pl.stxnext.grot.R;
import pl.stxnext.grot.enums.FieldType;
import pl.stxnext.grot.enums.Rotation;
import pl.stxnext.grot.view.GameButtonView;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameFragment extends Fragment {
    private static final int COUNT = 16;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillGamePlain((ViewGroup) view);
    }

    private void fillGamePlain(ViewGroup view) {
        for (int i = 0; i < COUNT; i++) {
            LinearLayout buttonLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.button_layout, null);
            GameButtonView gameButtonView = (GameButtonView) buttonLayout.findViewById(R.id.button);
            gameButtonView.setColor(getResources().getColor(randomField().getColorId()));
            gameButtonView.setRotation(randomRotation());
            gameButtonView.setId(i);
            gameButtonView.setTag(i);
            view.addView(buttonLayout);
        }
    }

    private FieldType randomField() {
        double random = Math.random();
        FieldType[] types = FieldType.values();
        int type = (int) (random * types.length);
        return types[type];
    }

    private Rotation randomRotation() {
        double random = Math.random();
        Rotation[] types = Rotation.values();
        int type = (int) (random * types.length);
        return types[type];
    }
}
