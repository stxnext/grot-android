package pl.stxnext.grot.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GamePlainModel implements Parcelable {
    private final int size;
    private final int area;
    private final List<GameFieldModel> fieldModels;
    private int moves;
    private int score;

    public GamePlainModel(int size) {
        this.size = size;
        this.area = size * size;
        this.fieldModels = new ArrayList<>(area);
    }

    public void addGameFieldModel(GameFieldModel model) {
        fieldModels.add(model);
    }

    public int getSize() {
        return size;
    }

    public int getArea() {
        return area;
    }

    public Iterator<GameFieldModel> getGamePlainIterator() {
        return fieldModels.iterator();
    }

    public GameFieldModel getFieldModel(int position) {
        if (position >= 0 && position < fieldModels.size()) {
            return fieldModels.get(position);
        }
        return null;
    }

    public int getMoves() {
        return moves;
    }

    public void setMoves(int moves) {
        this.moves = moves;
    }

    public int getScore() {
        return score;
    }

    private void setScore(int score) {
        this.score = score;
    }

    public void updateResults(int gainedScore, int gainedMoves) {
        setScore(getScore() + gainedScore);
        setMoves(getMoves() + gainedMoves);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(size);
        dest.writeInt(moves);
        dest.writeInt(score);
        dest.writeParcelableArray(fieldModels.toArray(new GameFieldModel[fieldModels.size()]), 0);
    }

    public static final Parcelable.Creator<GamePlainModel> CREATOR = new Parcelable.Creator<GamePlainModel>() {

        public GamePlainModel createFromParcel(Parcel in) {
            int size = in.readInt();
            int moves = in.readInt();
            int score = in.readInt();
            Parcelable[] gameFieldModels = in.readParcelableArray(GameFieldModel.class.getClassLoader());
            GamePlainModel r = new GamePlainModel(size);
            r.setMoves(moves);
            r.setScore(score);
            for(Parcelable gameFieldModel : gameFieldModels) {
                r.addGameFieldModel((GameFieldModel) gameFieldModel);
            }
            return r;
        }

        public GamePlainModel[] newArray(int size) {
            return new GamePlainModel[size];
        }
    };
}
