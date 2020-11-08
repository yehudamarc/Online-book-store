package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * A broadcast messages that is sent at every passed clock tick. This message must contain the
 * current tick (int).
 */
public class TickBroadcast implements Broadcast {
    private int currentTick;

    public TickBroadcast(int currentTick) {
        this.currentTick = currentTick;
    }

    public int getCurrentTick() {
        return this.currentTick;
    }
}
