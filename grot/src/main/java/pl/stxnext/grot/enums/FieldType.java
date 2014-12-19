package pl.stxnext.grot.enums;

import pl.stxnext.grot.R;

public enum FieldType {
    LOWEST(R.color.lowest_color), LOW(R.color.low_color), HIGH(R.color.high_color), HIGHEST(R.color.highest_color);

    private int colorId;

    private FieldType(int colorId) {
        this.colorId = colorId;
    }

    public int getColorId() {
        return colorId;
    }
}
