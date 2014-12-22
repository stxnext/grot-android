package pl.stxnext.grot.enums;

import pl.stxnext.grot.R;

public enum FieldType {
    LOWEST(R.color.lowest_color, 0.4, 1), LOW(R.color.low_color, 0.3, 2), HIGH(R.color.high_color, 0.2, 3), HIGHEST(R.color.highest_color, 0.1, 4);

    private final int colorId;
    private final double distribution;
    private final int points;

    private FieldType(int colorId, double distribution, int points) {
        this.colorId = colorId;
        this.distribution = distribution;
        this.points = points;
    }

    public int getColorId() {
        return colorId;
    }

    public double getDistribution() {
        return distribution;
    }

    public int getPoints() {
        return points;
    }
}
