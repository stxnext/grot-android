package pl.stxnext.grot.fragment;

import android.animation.Animator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.Iterator;
import java.util.List;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;
import pl.stxnext.grot.enums.Rotation;
import pl.stxnext.grot.game.GamePlainGenerator;
import pl.stxnext.grot.listener.GameStateChangedListener;
import pl.stxnext.grot.model.FieldTransition;
import pl.stxnext.grot.model.GameFieldModel;
import pl.stxnext.grot.model.GamePlainModel;
import pl.stxnext.grot.view.GameButtonView;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameFragment extends Fragment {

    private GameStateChangedListener listener;
    private GridLayout gridLayout;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.gridLayout = (GridLayout) view;
        this.handler = new Handler();
        fillGamePlain(gridLayout);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.listener = (GameStateChangedListener) getActivity();
        } catch (ClassCastException ex) {
            Log.e(AppConfig.DEBUG_TAG, "Error: Activity must implement GameStateChangedListener!");
            Log.w(AppConfig.DEBUG_TAG, "Class: GameFragment; method: onAttach() - fragment listener class cast exception");
            Log.w(AppConfig.DEBUG_TAG, ex.getMessage());
        }
    }

    private void fillGamePlain(ViewGroup view) {
        final GamePlainModel model = GamePlainGenerator.generateNewGamePlain();
        Iterator<GameFieldModel> iterator = model.getGamePlainIterator();
        for (int i = 0; iterator.hasNext(); i++) {
            GameFieldModel fieldModel = iterator.next();
            LinearLayout buttonLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.button_layout, null);
            final GameButtonView gameButtonView = (GameButtonView) buttonLayout.findViewById(R.id.button);
            gameButtonView.setModel(fieldModel);
            gameButtonView.setId(i);
            gameButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFieldPressed(gameButtonView.getId());
                }
            });
            view.addView(buttonLayout);
        }
        listener.onGameStarted(model);
    }

    public void updateGameBoard(final GamePlainModel model, final List<FieldTransition> fieldTransitions) {
        final Iterator<FieldTransition> iterator = fieldTransitions.iterator();
        if (iterator.hasNext()) {
            final FieldTransition fieldTransition = iterator.next();
            final GameButtonView gameButtonView = (GameButtonView) gridLayout.findViewById(fieldTransition.getPosition());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    configAnimation(gameButtonView, fieldTransition.getFieldModel().getRotation(), iterator, model, fieldTransitions);
                }
            });
        } else {
            listener.onAnimationEnd(model, fieldTransitions);
        }
    }

    private ViewPropertyAnimator configAnimation(View view, Rotation rotation, final Iterator<FieldTransition> iterator, final GamePlainModel model, final List<FieldTransition> fieldTransitions) {
        ViewPropertyAnimator animator = view.animate();
        animator.alpha(0).setDuration(600);
        switch (rotation) {
            case LEFT:
                animator.xBy(-view.getWidth());
                break;
            case RIGHT:
                animator.xBy(view.getWidth());
                break;
            case UP:
                animator.yBy(-view.getHeight());
                break;
            case DOWN:
                animator.yBy(view.getHeight());
                break;
        }
        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (iterator.hasNext()) {
                    final FieldTransition fieldTransition = iterator.next();
                    final GameButtonView gameButtonView = (GameButtonView) gridLayout.findViewById(fieldTransition.getPosition());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            configAnimation(gameButtonView, fieldTransition.getFieldModel().getRotation(), iterator, model, fieldTransitions);
                        }
                    }, 100);
                } else {
                    listener.onAnimationEnd(model, fieldTransitions);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return animator;
    }
}
