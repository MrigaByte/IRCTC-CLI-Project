package ticket.booking.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TrainServiceUtil {

    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);;
    private String TRAINS_PATH = ConfigLoader.get("db.trains.path");
    private List<Train> allTrainsList;



    public List<Train> loadAllTrainsListFromFile() throws IOException {
        // load the user from file to memory
        return allTrainsList = objectMapper.readValue(new File(TRAINS_PATH), new TypeReference<List<Train>>() {});
    }

    public void saveAllTrainsListToFile() throws IOException {
        objectMapper.writeValue(new File(TRAINS_PATH), allTrainsList);
    }
}
