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

    public void updateGamePlain(List<FieldTransition> fieldTransitions) {
        final Set<Integer> emptyPositions = new HashSet<>(fieldTransitions.size());
        final List<GameFieldModel> animationWaitList = new ArrayList<>();
        for (FieldTransition fieldTransition : fieldTransitions) {
            emptyPositions.add(fieldTransition.getPosition());
        }
        final Handler handler = new Handler();
        for (int x = 0; x < size; x++) {
            int gaps = 0;
            for (int y = size - 1; y >= 0; y--) {
                int position = y * size + x;
                if (emptyPositions.contains(position)) {
                    gaps++;
                } else if (gaps > 0) {
                    final int positionToSwap = (y + gaps) * size + x;
                    final GameFieldModel gameFieldModel = fieldModels.get(position);
                    emptyPositions.remove(positionToSwap);
                    gameFieldModel.animate(gaps, new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    GameFieldModel swapGameFieldModel = fieldModels.get(positionToSwap);
                                    swapGameFieldModel.setFieldType(gameFieldModel.getFieldType());
                                    swapGameFieldModel.setRotation(gameFieldModel.getRotation());
                                    swapGameFieldModel.notifyModelChanged(false);
                                    gameFieldModel.setFieldType(GamePlainGenerator.randomField());
                                    gameFieldModel.setRotation(GamePlainGenerator.randomRotation());
                                    gameFieldModel.notifyModelChanged(true);
                                    animationWaitList.remove(gameFieldModel);
                                }
                            }, 200);

                        }
                    });
                    animationWaitList.add(gameFieldModel);
                }
            }
        }
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
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Integer emptyPosition : emptyPositions) {
                            GameFieldModel fieldModel = fieldModels.get(emptyPosition);
                            fieldModel.setFieldType(GamePlainGenerator.randomField());
                            fieldModel.setRotation(GamePlainGenerator.randomRotation());
                            fieldModel.notifyModelChanged(true);
                        }
                    }
                });
            }
        });
        thread.start();
    }

}
