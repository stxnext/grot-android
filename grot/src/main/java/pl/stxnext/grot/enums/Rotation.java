package pl.stxnext.grot.enums;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public enum Rotation {
    LEFT(180), RIGHT(0), UP(270), DOWN(90);

    private final int degrees;

    private Rotation(int degrees) {
        this.degrees = degrees;
    }

    public int getRotation() {
        return degrees;
    }
}
