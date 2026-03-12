package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {

    //object mapper to map json data to entities
    private ObjectMapper objectMapper = new ObjectMapper();

    private List<User> userList;
    private User user;

    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    //constructor
    public UserBookingService(User user1)  throws IOException
    {
        this.user = user1;
        loadUsers();
    }

    //default constructor
    public UserBookingService() throws IOException{
        loadUsers();
    }

    public List<User> loadUsers() throws IOException{
        File users = new File(USERS_PATH);
        return objectMapper.readValue(users, new TypeReference<List<User>>() {});
    }

    private void saveUserListToFile() throws IOException {
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public Boolean cancelBooking() throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter ticket Id to cancel:");
        String ticketId = s.next();

        if (ticketId == null || ticketId.isEmpty()) {
            System.out.println("Invalid ticket id.");
            return Boolean.FALSE;
        }
        //because Strings are immutable

        List<Ticket> updatedTicketsBooked = user.getTicketsBooked().stream().filter(ticket -> !ticket.getTicketId().equals(ticketId)).toList();
        if (updatedTicketsBooked.size() < user.getTicketsBooked().size()) {
            user.setTicketsBooked(updatedTicketsBooked);
            saveUserListToFile();
            System.out.println("Ticket with ID " + ticketId + " has been cancelled.");
            return Boolean.TRUE;
        } else return Boolean.FALSE;

    }


}
