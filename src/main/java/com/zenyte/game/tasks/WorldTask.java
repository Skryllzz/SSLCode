package com.zenyte.game.tasks;

public interface WorldTask extends Runnable {
    default void stop() {
        final WorldTasksManager.WorldTaskInformation info = WorldTasksManager.MAIN_TASKS.get(this);
        if (info != null) {
            info.continueMaxCount = -1;
        }
    }
}
