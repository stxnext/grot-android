package pl.stxnext.grot.listener;

import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public interface GameStateChangedListener {
    void onGameStarted(GamePlainModel model);
}
