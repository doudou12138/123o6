package org.fffd.l23o6.util.strategy.train;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Nullable;


public class KSeriesSeatStrategy extends TrainSeatStrategy {
    public static final KSeriesSeatStrategy INSTANCE = new KSeriesSeatStrategy();
     
    private final Map<Integer, String> SOFT_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SLEEPER_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> SOFT_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> HARD_SEAT_MAP = new HashMap<>();
    private final Map<Integer, String> NO_SEAT_MAP = new HashMap<>();

    private final Map<KSeriesSeatType, Map<Integer, String>> TYPE_MAP = new HashMap<>() {{
        put(KSeriesSeatType.SOFT_SLEEPER_SEAT, SOFT_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.HARD_SLEEPER_SEAT, HARD_SLEEPER_SEAT_MAP);
        put(KSeriesSeatType.SOFT_SEAT, SOFT_SEAT_MAP);
        put(KSeriesSeatType.HARD_SEAT, HARD_SEAT_MAP);
        put(KSeriesSeatType.NO_SEAT,NO_SEAT_MAP);
    }};

    private final Map<KSeriesSeatType,Integer> type_price = new HashMap<>(){{
        put(KSeriesSeatType.SOFT_SLEEPER_SEAT,35);
        put(KSeriesSeatType.HARD_SLEEPER_SEAT,25);
        put(KSeriesSeatType.SOFT_SEAT,15);
        put(KSeriesSeatType.HARD_SEAT,10);
        put(KSeriesSeatType.NO_SEAT,8);
    }};

    private KSeriesSeatStrategy() {

        int counter = 0;

        for (String s : Arrays.asList("软卧1号上铺", "软卧2号下铺", "软卧3号上铺", "软卧4号上铺", "软卧5号上铺", "软卧6号下铺", "软卧7号上铺", "软卧8号上铺")) {
            SOFT_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("硬卧1号上铺", "硬卧2号中铺", "硬卧3号下铺", "硬卧4号上铺", "硬卧5号中铺", "硬卧6号下铺", "硬卧7号上铺", "硬卧8号中铺", "硬卧9号下铺", "硬卧10号上铺", "硬卧11号中铺", "硬卧12号下铺")) {
            HARD_SLEEPER_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("1车1座", "1车2座", "1车3座", "1车4座", "1车5座", "1车6座", "1车7座", "1车8座", "2车1座", "2车2座", "2车3座", "2车4座", "2车5座", "2车6座", "2车7座", "2车8座")) {
            SOFT_SEAT_MAP.put(counter++, s);
        }

        for (String s : Arrays.asList("3车1座", "3车2座", "3车3座", "3车4座", "3车5座", "3车6座", "3车7座", "3车8座", "3车9座", "3车10座", "4车1座", "4车2座", "4车3座", "4车4座", "4车5座", "4车6座", "4车7座", "4车8座", "4车9座", "4车10座")) {
            HARD_SEAT_MAP.put(counter++, s);
        }

        for(String s:Arrays.asList("3车无座1","3车无座2","3车无座3","3车无座4","4车无座1","4车无座2","4车无座3")){
            NO_SEAT_MAP.put(counter++, s );
        }
    }

    public enum KSeriesSeatType implements SeatType {
        SOFT_SLEEPER_SEAT("软卧"), HARD_SLEEPER_SEAT("硬卧"), SOFT_SEAT("软座"), HARD_SEAT("硬座"), NO_SEAT("无座");
        private String text;
        KSeriesSeatType(String text){
            this.text=text;
        }
        public String getText() {
            return this.text;
        }
        public static KSeriesSeatType fromString(String text) {
            for (KSeriesSeatType b : KSeriesSeatType.values()) {
                if (b.text.equalsIgnoreCase(text)) {
                    return b;
                }
            }
            return null;
        }
    }


    public @Nullable String allocSeat(int startStationIndex, int endStationIndex, KSeriesSeatType type, boolean[][] seatMap) {
        //endStationIndex - 1 = upper bound

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
                    seatMap[i][num] = false;
                }
                return seat_map.get(num);
            }

        }

        return null;
    }

    public Map<KSeriesSeatType, Integer> getLeftSeatCount(int startStationIndex, int endStationIndex, boolean[][] seatMap) {
        Map<KSeriesSeatType,Integer> result = new HashMap<>();
        for(KSeriesSeatType k:KSeriesSeatType.values()){
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
        return new boolean[stationCount - 1][SOFT_SLEEPER_SEAT_MAP.size() + HARD_SLEEPER_SEAT_MAP.size() + SOFT_SEAT_MAP.size() + HARD_SEAT_MAP.size()+NO_SEAT_MAP.size()];
    }

    public int getPrice(KSeriesSeatType type){
        return type_price.get(type);
    }

    public void releaseSeat(int startStationIndex,int endStationIndex,String seat,boolean[][] seats){
        int sequ = -1;
        for(Map<Integer,String> seat_map:Arrays.asList(HARD_SLEEPER_SEAT_MAP,SOFT_SLEEPER_SEAT_MAP,SOFT_SEAT_MAP,HARD_SEAT_MAP
        ,NO_SEAT_MAP)){
            for(Integer sequence:seat_map.keySet()){
                if(seat_map.get(sequence).equals(seat)){
                    sequ = sequence;
                }
            }
        }

        if(sequ!=-1){
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
