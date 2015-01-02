package pl.stxnext.grot.fragment;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    private Map<Rotation, AnimatorSet> animations;

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
        prepareAnimations();
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

    public void updateGameBoard(GamePlainModel model, final List<FieldTransition> fieldTransitions) {
        final Iterator<FieldTransition> iterator = addAnimationSetListeners(fieldTransitions);
        if (iterator.hasNext()) {
            FieldTransition fieldTransition = iterator.next();
            GameButtonView gameButtonView = (GameButtonView) gridLayout.findViewById(fieldTransition.getPosition());
            final AnimatorSet buttonAnimation = animations.get(fieldTransition.getFieldModel().getRotation());
            buttonAnimation.setTarget(gameButtonView);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    buttonAnimation.start();
                }
            });
        } else {
            listener.onAnimationEnd(fieldTransitions);
        }
    }

    private void prepareAnimations() {
        this.animations = new HashMap<>();
//        animations.put(Rotation.LEFT, createAnimationSet(R.animator.button_animation));
//        animations.put(Rotation.RIGHT, createAnimationSet(R.animator.button_animation));
//        animations.put(Rotation.UP, createAnimationSet(R.animator.button_animation));
//        animations.put(Rotation.DOWN, createAnimationSet(R.animator.button_animation));
        animations.put(Rotation.LEFT, createAnimationSet(R.animator.button_animation_left));
        animations.put(Rotation.RIGHT, createAnimationSet(R.animator.button_animation_right));
        animations.put(Rotation.UP, createAnimationSet(R.animator.button_animation_up));
        animations.put(Rotation.DOWN, createAnimationSet(R.animator.button_animation_down));
    }

    private AnimatorSet createAnimationSet(int animatorSetId) {
        return (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), animatorSetId);
    }

    private Iterator<FieldTransition> addAnimationSetListeners(final List<FieldTransition> fieldTransitions) {
        final Iterator<FieldTransition> iterator = fieldTransitions.iterator();
        for (AnimatorSet animatorSet : animations.values()) {
            animatorSet.removeAllListeners();
            animatorSet.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (iterator.hasNext()) {
                        FieldTransition fieldTransition = iterator.next();
                        final GameButtonView nextGameButtonView = (GameButtonView) gridLayout.findViewById(fieldTransition.getPosition());
                        final AnimatorSet buttonAnimation = animations.get(fieldTransition.getFieldModel().getRotation());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                buttonAnimation.setTarget(nextGameButtonView);
                                buttonAnimation.start();
                            }
                        }, 100);
                    } else {
                        listener.onAnimationEnd(fieldTransitions);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return iterator;
    }
}
