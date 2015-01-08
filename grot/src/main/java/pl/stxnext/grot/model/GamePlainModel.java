package pl.stxnext.grot.model;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import pl.stxnext.grot.game.GamePlainGenerator;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GamePlainModel {
    private final int size;
    private final int area;
    private final List<GameFieldModel> fieldModels;
    private int moves;
    private int score;

    public GamePlainModel(int size) {
        this.size = size;
        this.area = size * size;
        this.fieldModels = new ArrayList<>(area);
    }

    public void addGameFieldModel(GameFieldModel model) {
        fieldModels.add(model);
    }

    public int getSize() {
        return size;
    }

    public int getArea() {
        return area;
    }

    public Iterator<GameFieldModel> getGamePlainIterator() {
        return fieldModels.iterator();
    }

    public GameFieldModel getFieldModel(int position) {
        if (position >= 0 && position < fieldModels.size()) {
            return fieldModels.get(position);
        }
        return null;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void updateResults(int gainedScore, int gainedMoves) {
        setScore(getScore() + gainedScore);
        setMoves(getMoves() + gainedMoves);
    }

    public void updateGamePlain(List<FieldTransition> fieldTransitions, final GamePlainModelUpdateListener listener) {
        final Set<Integer> emptyPositions = new HashSet<>(fieldTransitions.size());
        final List<GameFieldModel> animationWaitList = new ArrayList<>();
        for (FieldTransition fieldTransition : fieldTransitions) {
            emptyPositions.add(fieldTransition.getPosition());
        }
        for (int x = 0; x < size; x++) {
            int gaps = 0;
            for (int y = size - 1; y >= 0; y--) {
                final int position = y * size + x;
                if (emptyPositions.contains(position)) {
                    gaps++;
                } else if (gaps > 0) {
                    final int positionToSwap = (y + gaps) * size + x;
                    final GameFieldModel gameFieldModel = fieldModels.get(position);
                    emptyPositions.remove(positionToSwap);
                    final int jumps = gaps;
                    gameFieldModel.animateFall(jumps, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            GameFieldModel swapGameFieldModel = fieldModels.get(positionToSwap);
                            swapGameFieldModel.setFieldType(gameFieldModel.getFieldType());
                            swapGameFieldModel.setRotation(gameFieldModel.getRotation());
                            swapGameFieldModel.notifyModelChanged(false);
                            boolean shouldBeMarkedAsEmpty = true;
                            int yPos = position / size - jumps;
                            int xPos = position % size;
                            for (; yPos >= 0; yPos--) {
                                int positionAbove = yPos * size + xPos;
                                if (!emptyPositions.contains(positionAbove)) {
                                    shouldBeMarkedAsEmpty = false;
                                }
                            }
                            if (shouldBeMarkedAsEmpty) {
                                emptyPositions.add(position);
                            }
                            animationWaitList.remove(gameFieldModel);

                        }
                    });
                    animationWaitList.add(gameFieldModel);
                }
            }
        }
        final Handler handler = new Handler();
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!animationWaitList.isEmpty()) {
                    try {
                        synchronized (this) {
                            wait(600);
                        }
                    } catch (InterruptedException e) {
                        Log.d("GROT", "InterruptedException in waiting thread");
                    }
                }
                for (Integer emptyPosition : emptyPositions) {
                    final GameFieldModel fieldModel = fieldModels.get(emptyPosition);
                    fieldModel.setFieldType(GamePlainGenerator.randomField());
                    fieldModel.setRotation(GamePlainGenerator.randomRotation());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fieldModel.notifyModelChanged(true);
                        }
                    }, (long) (Math.random() * 200));
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listener.onGamePlainUpdated();
                    }
                }, 200);
            }
        });
        thread.start();
    }

    public interface GamePlainModelUpdateListener {
        void onGamePlainUpdated();
    }

}
