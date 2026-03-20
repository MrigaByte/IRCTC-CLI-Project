package ticket.booking.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BookingServiceUtil {
    private ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private final String USERS_PATH = ConfigLoader.get("db.users.path");
    private final String TRAINS_PATH = ConfigLoader.get("db.trains.path");

    private List<User> userList;
    private List<Train> trainList;

    public List<Train> loadAllTrainsListFromFile() throws IOException {
        // load the user from file to memory
        InputStream is = TrainServiceUtil.class.getClassLoader().getResourceAsStream(TRAINS_PATH);
        return trainList = objectMapper.readValue(is, new TypeReference<List<Train>>() {});
    }
    public List<User> loadAllUsersListFromFile() throws IOException {
        // load the user from file to memory
        InputStream is = TrainServiceUtil.class.getClassLoader().getResourceAsStream(USERS_PATH);
        return userList = objectMapper.readValue(is, new TypeReference<List<User>>() {});
    }

    public void saveAllTrainsListToFile() throws IOException {
        objectMapper.writeValue(new File(TRAINS_PATH), trainList);
    }
}
