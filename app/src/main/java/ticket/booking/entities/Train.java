package ticket.booking.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Time;
import java.util.List;
import java.util.Map;

public class Train {

    private String trainId;
    private String trainNo;
    private List<List<Boolean>> seats;
    private Map<String, String> stationTime;
    private List<String> stations;
    private Map<String, List<List<Integer>>> seatMap;

    public Train(String trainId, String trainNo, List<List<Boolean>> seats, Map<String, String> stationTime, List<String> stations, Map<String, List<List<Integer>>> seatMap) {
        this.trainId = trainId;
        this.trainNo = trainNo;
        this.seats = seats;
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

    public List<List<Boolean>> getSeats() {
        return seats;
    }

    public void setSeats(List<List<Boolean>> seats) {
        this.seats = seats;
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