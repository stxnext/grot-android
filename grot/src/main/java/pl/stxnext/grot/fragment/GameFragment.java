package pl.stxnext.grot.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
        final Set<Integer> positions = new HashSet<>();
        final List<Animator> animators = new ArrayList<>();
        if (iterator.hasNext()) {
            final FieldTransition fieldTransition = iterator.next();
            final int position = fieldTransition.getPosition();
            positions.add(position);
            final GameButtonView gameButtonView = (GameButtonView) gridLayout.findViewById(position);
            final AnimatorSet animatorSet = configAnimation(gameButtonView, position, fieldTransition.getFieldModel().getRotation(), iterator, model, fieldTransitions, positions, animators);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    animatorSet.start();
                }
            });
        } else {
            listener.onAnimationEnd(model, fieldTransitions);
        }
    }

    private AnimatorSet configAnimation(View view, final int position, Rotation rotation, final Iterator<FieldTransition> iterator, final GamePlainModel model, final List<FieldTransition> fieldTransitions, final Set<Integer> positions, final List<Animator> animators) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
        ObjectAnimator transitionAnimator = null;
        int jumps = 1;
        if (iterator.hasNext()) {
            jumps = calculateAnimationJumps(position, rotation, positions, model);
            switch (rotation) {
                case LEFT:
                    transitionAnimator = ObjectAnimator.ofFloat(view, "translationX", -view.getWidth() * jumps);
                    break;
                case RIGHT:
                    transitionAnimator = ObjectAnimator.ofFloat(view, "translationX", view.getWidth() * jumps);
                    break;
                case UP:
                    transitionAnimator = ObjectAnimator.ofFloat(view, "translationY", -view.getHeight() * jumps);
                    break;
                case DOWN:
                    transitionAnimator = ObjectAnimator.ofFloat(view, "translationY", view.getHeight() * jumps);
                    break;
            }
        }

        AnimatorSet animator = new AnimatorSet();
        animator.setDuration(400 * jumps);
        if (transitionAnimator != null) {
            animator.playTogether(alphaAnimator, transitionAnimator);
        } else {
            animator.play(alphaAnimator);
        }
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (iterator.hasNext()) {
                    final FieldTransition fieldTransition = iterator.next();
                    final int position = fieldTransition.getPosition();
                    positions.add(position);
                    final GameButtonView gameButtonView = (GameButtonView) gridLayout.findViewById(position);
                    final AnimatorSet animatorSet = configAnimation(gameButtonView, position, fieldTransition.getFieldModel().getRotation(), iterator, model, fieldTransitions, positions, animators);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animatorSet.start();
                        }
                    }, animation.getDuration() / 2);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animators.remove(animation);
                if (animators.isEmpty()) {
                    listener.onAnimationEnd(model, fieldTransitions);
                }
            }
        });
        animators.add(animator);
        return animator;
    }

    private int calculateAnimationJumps(final int position, final Rotation rotation, final Set<Integer> positions, final GamePlainModel model) {
        int x = position % model.getSize();
        int y = position / model.getSize();
        int jumps = 1;
        do {
            switch (rotation) {
                case LEFT:
                    if (x > 0) {
                        x = x - 1;
                    } else {
                        return jumps;
                    }
                    break;
                case RIGHT:
                    if (x < model.getSize() - 1) {
                        x = x + 1;
                    } else {
                        return jumps;
                    }
                    break;
                case UP:
                    if (y > 0) {
                        y = y - 1;
                    } else {
                        return jumps;
                    }
                    break;
                case DOWN:
                    if (y < model.getSize() - 1) {
                        y = y + 1;
                    } else {
                        return jumps;
                    }
                    break;
            }
            int nextPosition = y * model.getSize() + x;
            if (positions.contains(nextPosition)) {
                jumps++;
            } else {
                break;
            }
        } while (true);
        return jumps;
    }
}
