package pl.stxnext.grot.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pl.stxnext.grot.config.AppConfig;
import pl.stxnext.grot.enums.Rotation;
import pl.stxnext.grot.game.GamePlainGenerator;
import pl.stxnext.grot.model.FieldTransition;
import pl.stxnext.grot.model.GameFieldModel;
import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameController {
    private final GamePlainGenerator gamePlainGenerator;
    private final GameControllerListener listener;
    private GamePlainModel gamePlainModel;

    public GameController(GameControllerListener listener) {
        this.gamePlainGenerator = new GamePlainGenerator();
        this.listener = listener;
    }

    public GamePlainModel getNewGamePlainModel() {
        return gamePlainGenerator.generateNewGamePlain();
    }

    public void setNewGamePlainModel(GamePlainModel model) {
        this.gamePlainModel = model;
    }

    public void updateGameState(final int position) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<FieldTransition> fieldTransitions = new ArrayList<>();
                int nextPosition = position;
                Set<Integer> visited = new HashSet<>(gamePlainModel.getArea());
                do {
                    GameFieldModel model = gamePlainModel.getFieldModel(nextPosition);
                    if (model != null) {
                        visited.add(nextPosition);
                        FieldTransition fieldTransition = new FieldTransition(nextPosition, model);
                        fieldTransitions.add(fieldTransition);
                        nextPosition = getNextPosition(model);
                        while (nextPosition != -1 && visited.contains(nextPosition)) {
                            nextPosition = getNextPosition(nextPosition, model.getRotation());
                        }
                    }
                } while (nextPosition != -1);
                calculatePoints(fieldTransitions);
                listener.updateGameInfo(gamePlainModel, fieldTransitions);
            }
        });
        thread.start();
    }

    public void updateGamePlain(List<FieldTransition> fieldTransitions) {
        if (gamePlainModel.getMoves() == 0) {
            listener.onGameFinished(gamePlainModel);
        } else {
            final Set<Integer> emptyPositions = new HashSet<>(fieldTransitions.size());
            final List<GameFieldModel> animationWaitList = new ArrayList<>();
            for (FieldTransition fieldTransition : fieldTransitions) {
                emptyPositions.add(fieldTransition.getPosition());
            }
            final int size = gamePlainModel.getSize();
            for (int x = 0; x < size; x++) {
                int gaps = 0;
                for (int y = size - 1; y >= 0; y--) {
                    final int position = y * size + x;
                    if (emptyPositions.contains(position)) {
                        gaps++;
                    } else if (gaps > 0) {
                        final int positionToSwap = (y + gaps) * size + x;
                        final GameFieldModel gameFieldModel = gamePlainModel.getFieldModel(position);
                        emptyPositions.remove(positionToSwap);
                        final int jumps = gaps;
                        gameFieldModel.animateFall(jumps, new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                GameFieldModel swapGameFieldModel = gamePlainModel.getFieldModel(positionToSwap);
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
                                wait(AppConfig.ANIMATION_DURATION);
                            }
                        } catch (InterruptedException e) {
                            Log.d(AppConfig.DEBUG_TAG, "InterruptedException in waiting thread");
                        }
                    }
                    for (Integer emptyPosition : emptyPositions) {
                        final GameFieldModel fieldModel = gamePlainModel.getFieldModel(emptyPosition);
                        fieldModel.setFieldType(gamePlainGenerator.randomField());
                        fieldModel.setRotation(gamePlainGenerator.randomRotation());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                fieldModel.notifyModelChanged(true);
                            }
                        }, (long) (Math.random() * AppConfig.FADE_IN_ANIMATION_DELAY));
                    }
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onGamePlainUpdated();
                        }
                    }, AppConfig.FADE_IN_ANIMATION_DELAY);
                }
            });
            thread.start();
        }
    }

    private void calculatePoints(List<FieldTransition> fieldTransitions) {
        final int size = gamePlainModel.getSize();
        int gainedScore = 0;
        Map<Integer, Integer> rows = new HashMap<>(size);
        Map<Integer, Integer> cols = new HashMap<>(size);
        for (FieldTransition fieldTransition : fieldTransitions) {
            gainedScore += fieldTransition.getFieldModel().getFieldType().getPoints();
            int row = fieldTransition.getPosition() / size;
            if (rows.containsKey(row)) {
                rows.put(row, rows.get(row) + 1);
            } else {
                rows.put(row, 1);
            }
            int col = fieldTransition.getPosition() % size;
            if (cols.containsKey(col)) {
                cols.put(col, cols.get(col) + 1);
            } else {
                cols.put(col, 1);
            }
        }
        for (Integer row : rows.keySet()) {
            if (rows.get(row) == size) {
                gainedScore += size * 10;
            }
        }
        for (Integer col : cols.keySet()) {
            if (cols.get(col) == size) {
                gainedScore += size * 10;
            }
        }

        synchronized (this) {
            int threshold = (int) (Math.floor((gainedScore + gamePlainModel.getScore()) / (5 * gamePlainModel.getArea())) + size - 1);
            int gainedMoves = Math.max(0, fieldTransitions.size() - threshold);
            gainedMoves--;
            gamePlainModel.updateResults(gainedScore, gainedMoves);
        }
    }

    private int getNextPosition(GameFieldModel model) {
        Point point = model.getPoint();
        return getNextPosition(point.x, point.y, model.getRotation());
    }

    private int getNextPosition(int position, Rotation rotation) {
        int size = gamePlainModel.getSize();
        int x = position % size;
        int y = position / size;
        return getNextPosition(x, y, rotation);
    }

    private int getNextPosition(int x, int y, Rotation rotation) {
        final int size = gamePlainModel.getSize();
        switch (rotation) {
            case LEFT:
                if (x == 0) {
                    return -1;
                } else {
                    return calculatePosition(x - 1, y);
                }
            case RIGHT:
                if (x == size - 1) {
                    return -1;
                } else {
                    return calculatePosition(x + 1, y);
                }
            case UP:
                if (y == 0) {
                    return -1;
                } else {
                    return calculatePosition(x, y - 1);
                }
            case DOWN:
                if (y == size - 1) {
                    return -1;
                } else {
                    return calculatePosition(x, y + 1);
                }
        }
        return -1;
    }

    private int calculatePosition(int x, int y) {
        return y * gamePlainModel.getSize() + x;
    }

    public interface GameControllerListener {

        void updateGameInfo(GamePlainModel model, List<FieldTransition> fieldTransitions);

        void onGameFinished(GamePlainModel model);

        void onGamePlainUpdated();

    }
}
