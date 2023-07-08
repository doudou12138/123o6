package org.fffd.l23o6.util.strategy.train;

import jakarta.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public abstract class TrainSeatStrategy {

    public interface SeatType {
        public String getText();
    }

    public @Nullable
    String allocSeat(int startStationIndex, int endStationIndex, KSeriesSeatStrategy.KSeriesSeatType type, boolean[][] seatMap) {
        return null;
    }

    public boolean[][] initSeatMap(int stationCount) {
        return null;
    }

    public int getPrice(KSeriesSeatStrategy.KSeriesSeatType type) {
        return -1;
    }

    public void releaseSeat(int startStationIndex, int endStationIndex, String seat, boolean[][] seats) {
    }

    public TrainSeatStrategy getInstance(){
        return null;
    }
}
