package ticket.booking.services;

import ticket.booking.entities.Train;
import ticket.booking.util.TrainServiceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainService {

    TrainServiceUtil trainServiceUtil = new TrainServiceUtil();
    private List<Train> allTrainsList;

    public TrainService() throws IOException {
        allTrainsList =  trainServiceUtil.loadAllTrainsListFromFile();
    }

    public List<Train> searchTrains(String source, String destination){
        List<Train> foundTrains = allTrainsList.stream().filter(train -> {
            return train.getStations().contains(source.toLowerCase()) && train.getStations().contains(destination.toLowerCase()) && train.getStations().indexOf(source.toLowerCase()) < train.getStations().indexOf(destination.toLowerCase());
        }).toList();

        return foundTrains;
    }
}
