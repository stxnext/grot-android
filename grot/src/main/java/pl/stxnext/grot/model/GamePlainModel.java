package pl.stxnext.grot.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GamePlainModel {
    private final int size;
    private final List<GameFieldModel> fieldModels;
    private int moves;
    private int score;

    public GamePlainModel(int size) {
        this.size = size;
        this.fieldModels = new ArrayList<>(size);
    }

    public void addGameFieldModel(GameFieldModel model) {
        fieldModels.add(model);
    }

    public int getSize() {
        return size;
    }

    public Iterator<GameFieldModel> getGamePlainIterator() {
        return fieldModels.iterator();
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
}
