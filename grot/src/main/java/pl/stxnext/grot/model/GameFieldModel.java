package pl.stxnext.grot.model;

import android.animation.AnimatorListenerAdapter;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

import pl.stxnext.grot.enums.FieldType;
import pl.stxnext.grot.enums.Rotation;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameFieldModel implements Parcelable {

    private FieldType fieldType;
    private Rotation rotation;
    private Point point;
    private ModelChangedListener listener;

    public GameFieldModel() {
    }

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

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public void setListener(ModelChangedListener listener) {
        this.listener = listener;
    }

    public void notifyModelChanged(boolean animateAlpha) {
        if (listener != null) {
            listener.onModelChanged(this, animateAlpha);
        }
    }

    public void animateFall(int jumps, AnimatorListenerAdapter animatorListener) {
        if (listener != null) {
            listener.animateFall(jumps, animatorListener);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(fieldType);
        dest.writeSerializable(rotation);
        dest.writeParcelable(point, 0);
    }

    public static final Parcelable.Creator<GameFieldModel> CREATOR = new Parcelable.Creator<GameFieldModel>() {

        public GameFieldModel createFromParcel(Parcel in) {
            FieldType fieldType = (FieldType) in.readSerializable();
            Rotation rotation = (Rotation) in.readSerializable();
            Point point = in.readParcelable(Point.class.getClassLoader());
            GameFieldModel r = new GameFieldModel();
            r.setFieldType(fieldType);
            r.setRotation(rotation);
            r.setPoint(point);
            return r;
        }

        public GameFieldModel[] newArray(int size) {
            return new GameFieldModel[size];
        }
    };

    public interface ModelChangedListener {
        void onModelChanged(GameFieldModel model, boolean animateAlpha);
        void animateFall(int jumps, AnimatorListenerAdapter animatorListener);
    }
}
