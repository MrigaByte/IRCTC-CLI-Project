package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.ConfigLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookingService {

    private ObjectMapper objectMapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    private List<User> userList;
    private List<Train> trainList;

    private final String USERS_PATH = ConfigLoader.get("db.users.path");
    private final String TRAINS_PATH = ConfigLoader.get("db.trains.path");

    public BookingService() throws IOException {
        // Load both lists on startup
        InputStream usersStream = getClass().getClassLoader().getResourceAsStream(USERS_PATH);
        userList = objectMapper.readValue(usersStream, new TypeReference<List<User>>() {});

        InputStream trainsStream = getClass().getClassLoader().getResourceAsStream(TRAINS_PATH);
        trainList = objectMapper.readValue(trainsStream, new TypeReference<List<Train>>() {});
    }

    /**
     * Books seats on a train for a user.
     *
     * @param user           the logged-in user making the booking
     * @param train          the train selected by the user
     * @param source         source station
     * @param destination    destination station
     * @param seatType       e.g. "SL", "3AC", "2AC", "1AC"
     * @param numberOfSeats  how many seats to book
     * @return true if booking succeeded, false otherwise
     */
    public Boolean bookTicket(User user, Train train, String source, String destination, String seatType, int numberOfSeats) {
        try {
            // Step 1 — Check if the requested seat type exists on this train
            if (!train.getSeatMap().containsKey(seatType)) {
                System.out.println("Seat type " + seatType + " is not available on this train.");
                return false;
            }

            // Step 2 — Get the 2D seat grid for the requested seat type
            List<List<Integer>> seats = train.getSeatMap().get(seatType);

            // Step 3 — Loop through the grid and collect free seats (value = 0)
            List<String> allocatedSeats = new ArrayList<>();

            for (int row = 0; row < seats.size(); row++) {
                for (int col = 0; col < seats.get(row).size(); col++) {
                    if (seats.get(row).get(col) == 0) {
                        // This seat is free — add to allocated list
                        allocatedSeats.add(seatType + "-" + row + "-" + col);

                        // Step 4 — Mark it as booked immediately
                        seats.get(row).set(col, 1);

                        // Stop once we have enough seats
                        if (allocatedSeats.size() == numberOfSeats) {
                            break;
                        }
                    }
                }
                if (allocatedSeats.size() == numberOfSeats) {
                    break;
                }
            }

            // Step 5 — Check if we found enough seats
            if (allocatedSeats.size() < numberOfSeats) {
                System.out.println("Not enough seats available. Only " + allocatedSeats.size() + " seats found.");
                return false;
            }

            // Step 6 — Create a Ticket object with all booking info
            Ticket ticket = new Ticket(
                    UUID.randomUUID().toString(),   // unique ticketId
                    user.getUserID(),               // userId
                    source,                         // source station
                    destination,                    // destination station
                    LocalDate.now().toString(),     // dateOfTravel (today for simplicity)
                    seatType,                       // e.g. "SL"
                    allocatedSeats,                 // e.g. ["SL-0-2", "SL-0-3"]
                    LocalDate.now().toString(),     // bookingDate
                    train                           // the train object
            );

            // Step 7 — Add ticket to user's bookings
            user.getTicketsBooked().add(ticket);

            // Step 8 — Save updated user to users.json
            saveUserToFile(user);

            // Step 9 — Save updated train (with booked seats) to trains.json
            saveTrainToFile(train);

            System.out.println("Booking successful! Your seats: " + allocatedSeats);
            return true;

        } catch (IOException e) {
            System.out.println("Booking failed due to a system error.");
            return false;
        }
    }

    /**
     * Cancels a booking by ticketId for the logged-in user.
     * Also frees up the seats on the train.
     */
    public Boolean cancelBooking(User user, String ticketId) {
        try {
            // Step 1 — Find the ticket in user's bookings
            List<Ticket> tickets = user.getTicketsBooked();
            Ticket ticketToCancel = null;

            for (Ticket ticket : tickets) {
                if (ticket.getTicketId().equals(ticketId)) {
                    ticketToCancel = ticket;
                    break;
                }
            }

            // Step 2 — If ticket not found, return false
            if (ticketToCancel == null) {
                System.out.println("No ticket found with ID: " + ticketId);
                return false;
            }

            // Step 3 — Free up the seats on the train
            Train train = ticketToCancel.getTrain();
            String seatType = ticketToCancel.getSeatType();
            List<String> seatNumbers = ticketToCancel.getSeatNumbers();

            if (train != null && seatNumbers != null) {
                List<List<Integer>> seats = train.getSeatMap().get(seatType);
                for (String seatNumber : seatNumbers) {
                    // seatNumber format is e.g. "SL-2-4" → row=2, col=4
                    String[] parts = seatNumber.split("-");
                    // parts[0] = seatType, parts[1] = row, parts[2] = col
                    // Note: for seat types like "3AC", format is "3AC-2-4"
                    int row = Integer.parseInt(parts[parts.length - 2]);
                    int col = Integer.parseInt(parts[parts.length - 1]);
                    seats.get(row).set(col, 0); // mark seat as free again
                }
                saveTrainToFile(train);
            }

            // Step 4 — Remove ticket from user's bookings
            tickets.remove(ticketToCancel);
            user.setTicketsBooked(tickets);

            // Step 5 — Save updated user
            saveUserToFile(user);

            System.out.println("Ticket " + ticketId + " has been cancelled successfully.");
            return true;

        } catch (IOException e) {
            System.out.println("Cancellation failed due to a system error.");
            return false;
        }
    }

    /**
     * Finds the user in userList by name, updates their record, and saves the full list to file.
     */
    private void saveUserToFile(User updatedUser) throws IOException {
        // Find and replace the user in the list by userId
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUserID().equals(updatedUser.getUserID())) {
                userList.set(i, updatedUser);
                break;
            }
        }
        File usersFile = new File(getClass().getClassLoader().getResource(USERS_PATH).getFile());
        objectMapper.writeValue(usersFile, userList);
    }

    /**
     * Finds the train in trainList by trainId, updates its record, and saves the full list to file.
     */
    private void saveTrainToFile(Train updatedTrain) throws IOException {
        // Find and replace the train in the list by trainId
        for (int i = 0; i < trainList.size(); i++) {
            if (trainList.get(i).getTrainId().equals(updatedTrain.getTrainId())) {
                trainList.set(i, updatedTrain);
                break;
            }
        }
        File trainsFile = new File(getClass().getClassLoader().getResource(TRAINS_PATH).getFile());
        objectMapper.writeValue(trainsFile, trainList);
    }
}