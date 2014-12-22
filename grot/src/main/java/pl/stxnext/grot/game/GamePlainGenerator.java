package pl.stxnext.grot.game;

import pl.stxnext.grot.enums.FieldType;
import pl.stxnext.grot.enums.Rotation;
import pl.stxnext.grot.model.GameFieldModel;
import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GamePlainGenerator {
    private static final int COUNT = 16;
    private static final int MOVES = 5;

    public static GamePlainModel generateGamePlain() {
        GamePlainModel model = new GamePlainModel(COUNT);
        for (int i = 0; i < COUNT; i++) {
            model.addGameFieldModel(getRandomModel());
        }
        model.setMoves(MOVES);
        return model;
    }

    private static GameFieldModel getRandomModel() {
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
