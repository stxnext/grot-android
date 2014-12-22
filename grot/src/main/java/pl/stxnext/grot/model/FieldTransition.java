package pl.stxnext.grot.model;

import pl.stxnext.grot.enums.FieldType;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class FieldTransition {
    private final int position;
    private final GameFieldModel fieldModel;

    public FieldTransition(int position, GameFieldModel fieldModel) {
        this.position = position;
        this.fieldModel = fieldModel;
    }

    public int getPosition() {
        return position;
    }

    public GameFieldModel getFieldModel() {
        return fieldModel;
    }
}
