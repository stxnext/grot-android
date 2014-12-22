package pl.stxnext.grot.game;

import android.graphics.Point;

import java.util.List;

import pl.stxnext.grot.enums.FieldType;
import pl.stxnext.grot.enums.Rotation;
import pl.stxnext.grot.model.FieldTransition;
import pl.stxnext.grot.model.GameFieldModel;
import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GamePlainGenerator {
    private static final int SIZE = 4;
    private static final int MOVES = 5;

    public static GamePlainModel generateNewGamePlain() {
        GamePlainModel model = new GamePlainModel(SIZE);
        for (int i = 0; i < model.getArea(); i++) {
            GameFieldModel fieldModel = getRandomFieldModel();
            int column = i % SIZE;
            int row = i / SIZE;
            Point point = new Point(column, row);
            fieldModel.setPoint(point);
            model.addGameFieldModel(fieldModel);
        }
        model.setMoves(MOVES);
        return model;
    }

    public static GamePlainModel updateGamePlain(GamePlainModel model, List<FieldTransition> fieldTransitions) {
        for (FieldTransition fieldTransition : fieldTransitions) {
            GameFieldModel fieldModel = fieldTransition.getFieldModel();
            fieldModel.setFieldType(randomField());
            fieldModel.setRotation(randomRotation());
            fieldModel.notifyModelChanged();
        }
        return model;
    }

    private static GameFieldModel getRandomFieldModel() {
        return new GameFieldModel(randomField(), randomRotation());
    }

    private static FieldType randomField() {
        double randomValue = Math.random();
        double counter = 0.0;
        for (FieldType type : FieldType.values()) {
            counter += type.getDistribution();
            if (randomValue <= counter) {
                return type;
            }
        }
        return FieldType.HIGHEST;
    }

    private static Rotation randomRotation() {
        double random = Math.random();
        Rotation[] types = Rotation.values();
        int type = (int) (random * types.length);
        return types[type];
    }
}
