package pl.stxnext.grot.model;

import pl.stxnext.grot.enums.FieldType;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class FieldTransition {
    private final int position;
    private final FieldType fieldType;

    public FieldTransition(int position, FieldType fieldType) {
        this.position = position;
        this.fieldType = fieldType;
    }

    public int getPosition() {
        return position;
    }

    public FieldType getFieldType() {
        return fieldType;
    }
}
