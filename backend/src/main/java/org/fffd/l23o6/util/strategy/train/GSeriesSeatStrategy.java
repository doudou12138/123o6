package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;


public class GSeriesSeatStrategy extends TrainSeatStrategy {
    public static final GSeriesSeatStrategy INSTANCE = new GSeriesSeatStrategy();
     
    private final Map<Integer, String> BUSINESS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> FIRST_CLASS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SECOND_CLASS_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> NO_SEAT_MAP = new HashMap<>();

    private final Map<GSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(GSeriesSeatType.BUSINESS_SEAT, BUSINESS_SEAT_MAP);
        put(GSeriesSeatType.FIRST_CLASS_SEAT, FIRST_CLASS_SEAT_MAP);
        put(GSeriesSeatType.SECOND_CLASS_SEAT, SECOND_CLASS_SEAT_MAP);
        put(GSeriesSeatType.NO_SEAT,NO_SEAT_MAP);
    }};

    private final Map<GSeriesSeatType, Integer> type_price = new HashMap<>(){{
        put(GSeriesSeatType.BUSINESS_SEAT,60);
        put(GSeriesSeatType.FIRST_CLASS_SEAT,40);
        put(GSeriesSeatType.SECOND_CLASS_SEAT,25);
        put(GSeriesSeatType.NO_SEAT,25);
    }};


    private GSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("1车1A","1车1C","1车1F")) {
            BUSINESS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("2车1A","2车1C","2车1D","2车1F","2车2A","2车2C","2车2D","2车2F","3车1A","3车1C","3车1D","3车1F")) {
            FIRST_CLASS_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("4车1A","4车1B","4车1C","4车1D","4车2F","4车2A","4车2B","4车2C","4车2D","4车2F","4车3A","4车3B","4车3C","4车3D","4车3F")) {
            SECOND_CLASS_SEAT_MAP.put(counter++, s);
        }

        for(String s:Arrays.asList("2车无座1","2车无座2","2车无座3","4车无座1","4车无座2","4车无座3","4车无座4","4车无座5")){
            NO_SEAT_MAP.put(counter++,s);
        }

    }

    public enum GSeriesSeatType implements SeatType {
        BUSINESS_SEAT("商务座"), FIRST_CLASS_SEAT("一等座"), SECOND_CLASS_SEAT("二等座"), NO_SEAT("无座");
        private String text;
        GSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static GSeriesSeatType fromString(String text) {
            for (GSeriesSeatType b : GSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, GSeriesSeatType type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound
        // TODO:finished
        //find the seat and change the state of the seat
        Map<Integer,String> seat_map = TYPE_MAP.get(type);
        for(Integer num:seat_map.keySet()){
            boolean able = true;
            for(int i=startStationIndex;i<endStationIndex;++i){
                if(!seatMap[i][num]){

                }else{
                    able=false;
                }
            }
            if(able){
                for(int i=startStationIndex;i<endStationIndex;++i){
                    seatMap[i][num] = true;
                }
                return seat_map.get(num);
            }

        }

        return null;
    }

    public Map<GSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        // TODO:finished
        Map<GSeriesSeatType,Integer> result = new HashMap<>();
        for(GSeriesSeatType k: GSeriesSeatType.values()){
            Map<Integer,String> seat_map=TYPE_MAP.get(k);
            int count=0;
            for(Integer s:seat_map.keySet()){
                boolean able = true;
                for(int i=startStationIndex;i<endStationIndex;++i){
                    if(!seatMap[i][s]){

                    }else{
                        able=false;
                        break;
                    }
                }
                if(able){
                    count++;
                }
            }
            result.put(k,count);
        }
        return result;
    }

    public boolean[][] initSeatMap(int stationCount) {
        return new boolean[stationCount - 1][BUSINESS_SEAT_MAP.size() + FIRST_CLASS_SEAT_MAP.size() + SECOND_CLASS_SEAT_MAP.size()+ NO_SEAT_MAP.size()];
    }

    public int getPrice(GSeriesSeatType type){
        return type_price.get(type);
    }

    public void releaseSeat(int startStationIndex,int endStationIndex,String seat,boolean[][] seats){
        int sequ = -1;
        for(Map<Integer,String> seat_map:Arrays.asList(BUSINESS_SEAT_MAP,FIRST_CLASS_SEAT_MAP,SECOND_CLASS_SEAT_MAP,NO_SEAT_MAP)){
            for(Integer sequence:seat_map.keySet()){
                if(seat_map.get(sequence).equals(seat)){
                    sequ = sequence;
                    System.err.println(sequ);
                }
            }
        }

        if(sequ!=-1){
            System.err.println(startStationIndex+""+endStationIndex);
            for(int i=startStationIndex;i<endStationIndex;++i){
                if(seats[i][sequ]){
                    seats[i][sequ]=false;
                }else{
                    System.err.println("error");
                }
            }
        }else{
            System.err.println("error");
        }

    }
}
