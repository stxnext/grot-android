package pl.stxnext.grot.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        //TODO Add arrows falling
        for (FieldTransition fieldTransition : fieldTransitions) {
            GameFieldModel fieldModel = fieldTransition.getFieldModel();
            fieldModel.setFieldType(GamePlainGenerator.randomField());
            fieldModel.setRotation(GamePlainGenerator.randomRotation());
            fieldModel.notifyModelChanged();
        }
    }
}
