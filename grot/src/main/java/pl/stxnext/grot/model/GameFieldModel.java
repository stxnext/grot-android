package pl.stxnext.grot.model;

import pl.stxnext.grot.enums.FieldType;
import pl.stxnext.grot.enums.Rotation;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameFieldModel {

    private FieldType fieldType;
    private Rotation rotation;

    public GameFieldModel() {}

    public GameFieldModel(FieldType fieldType, Rotation rotation) {
        this.fieldType = fieldType;
        this.rotation = rotation;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }
}
