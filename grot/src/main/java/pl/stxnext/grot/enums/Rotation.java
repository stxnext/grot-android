package pl.stxnext.grot.enums;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public enum Rotation {
    LEFT(0), RIGHT(180), UP(270), DOWN(90);

    private final int degrees;

    private Rotation(int degrees) {
        this.degrees = degrees;
    }

    public int getRotation() {
        return degrees;
    }
}
