package pl.stxnext.grot.listener;

import pl.stxnext.grot.model.FieldTransition;
import pl.stxnext.grot.model.GameFieldModel;
import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public interface GameStateChangedListener {
    void onGameStarted(GamePlainModel model);

    void onFieldPressed(int position);

}
