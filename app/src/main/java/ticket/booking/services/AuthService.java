package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthService {

    private List<User> userList;
    private static final String USER_FILE_PATH = "app/src/main/java/ticket/booking/localDb/users.json";
    private ObjectMapper objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);;


    public AuthService() throws IOException {
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        // load the user from file to memory
        userList = objectMapper.readValue(new File(USER_FILE_PATH), new TypeReference<List<User>>() {});
    }

    private void saveUserListToFile() throws IOException {
        objectMapper.writeValue(new File(USER_FILE_PATH), userList);
    }



    public Boolean signUp(String name, String password) {
        boolean userExists = userList.stream().anyMatch(user -> user.getName().equalsIgnoreCase(name));

        if(userExists){
            System.out.println("Username already taken. Please choose another.");
            return false;
        }

        try {
            User newUser = new User(name, password, UserServiceUtil.hashPassword(password), new ArrayList<>(), UUID.randomUUID().toString());
            userList.add(newUser);
            saveUserListToFile();
            System.out.println("Signup Successful! Please login.");
            return true;
        }
        catch(IOException ex) {
            System.out.println("Signup failed due to system error!");
            return false;
        }
    }

    public Optional<User> login(String name, String password) {
        Optional<User> foundUser = userList.stream().filter(user -> user.getName().equals(name) && UserServiceUtil.checkPassword(password, user.getHashedPassword())).findFirst();

        if(foundUser.isPresent()){
            System.out.println("Login Successful! Welcome");
        }
        else{
            System.out.println("Invalid username or password");
        }

        return foundUser;
    }
}
