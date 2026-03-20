package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.sql.Time;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Train {

    private String trainId;
    private String trainNo;
    private Map<String, String> stationTime;
    private List<String> stations;
    private Map<String, List<List<Integer>>> seatMap;  // e.g. {"SL": [[0,0,0],[0,0,0]], "3AC": [[0,0],[0,0]]}

    public Train(String trainId, String trainNo, List<List<Boolean>> seats, Map<String, String> stationTime, List<String> stations, Map<String, List<List<Integer>>> seatMap) {
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.stationTime = stationTime;
        this.stations = stations;
        this.seatMap = seatMap;
    }

    public Train() {
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getTrainNo() {
        return trainNo;
    }

    public void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public Map<String, String> getStationTime() {
        return stationTime;
    }

    public void setStationTime(Map<String, String> stationTime) {
        this.stationTime = stationTime;
    }

    public List<String> getStations() {
        return stations;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }

    public Map<String, List<List<Integer>>> getSeatMap() {
        return seatMap;
    }

    public void setSeatMap(Map<String, List<List<Integer>>> seatMap) {
        this.seatMap = seatMap;
    }
}