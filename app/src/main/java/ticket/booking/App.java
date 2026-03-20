package ticket.booking;

import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.AuthService;
import ticket.booking.services.BookingService;
import ticket.booking.services.TrainService;

import java.io.IOException;
import java.util.*;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");

        Scanner scanner = new Scanner(System.in);
        AuthService authService;
        TrainService trainService;
        BookingService bookingService;

        // Initialise all services on startup
        try {
            authService = new AuthService();
        } catch (IOException ex) {
            System.out.println("Failed to load user data. Exiting...");
            return;
        }
        try {
            trainService = new TrainService();
        } catch (IOException e) {
            System.out.println("Failed to load train data. Exiting...");
            return;
        }
        try {
            bookingService = new BookingService();
        } catch (IOException e) {
            System.out.println("Failed to load booking service. Exiting...");
            return;
        }

        User loggedInUser = null;
        Train trainSelectedForBooking = null;

        int option = 0;
        while (option != 8) {
            System.out.println("\n==== Train Booking System ====");
            System.out.println("1. Sign Up");
            System.out.println("2. Login");
            System.out.println("3. Search Trains");
            System.out.println("4. Book a Ticket");
            System.out.println("5. View My Bookings");
            System.out.println("6. Cancel a Booking");
            System.out.println("7. Logout");
            System.out.println("8. Exit");
            option = scanner.nextInt();

            switch (option) {

                case 1:
                    System.out.println("Enter username:");
                    String signUpName = scanner.next();
                    System.out.println("Enter password:");
                    String signUpPassword = scanner.next();
                    authService.signUp(signUpName, signUpPassword);
                    break;

                case 2:
                    System.out.println("Enter username:");
                    String loginName = scanner.next();
                    System.out.println("Enter password:");
                    String loginPassword = scanner.next();
                    Optional<User> result = authService.login(loginName, loginPassword);
                    if (result.isPresent()) {
                        loggedInUser = result.get();
                        System.out.println("Welcome, " + loggedInUser.getName() + "!");
                    } else {
                        System.out.println("Invalid credentials.");
                    }
                    break;

                case 3:
                    // Search trains — no login required
                    System.out.println("Enter source station:");
                    String searchSource = scanner.next();
                    System.out.println("Enter destination station:");
                    String searchDest = scanner.next();

                    List<Train> searchResult = trainService.searchTrains(searchSource, searchDest);

                    if (searchResult.isEmpty()) {
                        System.out.println("No trains found. Please try a different route.");
                    } else {
                        System.out.printf("%d train(s) found:%n", searchResult.size());
                        for (int i = 0; i < searchResult.size(); i++) {
                            Train t = searchResult.get(i);
                            System.out.println((i + 1) + ". Train ID: " + t.getTrainId()
                                    + " (" + t.getTrainNo() + ")"
                                    + " | Route: " + String.join(" -> ", t.getStations()));
                        }
                    }
                    break;

                case 4:
                    // Book a ticket — login required
                    if (loggedInUser == null) {
                        System.out.println("Please login first (option 2).");
                        break;
                    }

                    // Step 1 — Search trains
                    System.out.println("Enter source station:");
                    String bookSource = scanner.next();
                    System.out.println("Enter destination station:");
                    String bookDest = scanner.next();

                    List<Train> bookingTrains = trainService.searchTrains(bookSource, bookDest);

                    if (bookingTrains.isEmpty()) {
                        System.out.println("No trains found for this route.");
                        break;
                    }

                    // Step 2 — Display trains
                    System.out.printf("%d train(s) found:%n", bookingTrains.size());
                    for (int i = 0; i < bookingTrains.size(); i++) {
                        Train t = bookingTrains.get(i);
                        System.out.println((i + 1) + ". Train ID: " + t.getTrainId()
                                + " (" + t.getTrainNo() + ")"
                                + " | Route: " + String.join(" -> ", t.getStations()));
                    }

                    // Step 3 — Select a train
                    System.out.println("Select a train (enter number):");
                    int trainChoice = scanner.nextInt();
                    if (trainChoice < 1 || trainChoice > bookingTrains.size()) {
                        System.out.println("Invalid selection.");
                        break;
                    }
                    trainSelectedForBooking = bookingTrains.get(trainChoice - 1);

                    // Step 4 — Display available seat types
                    System.out.println("Available seat types:");
                    List<String> seatTypes = new ArrayList<>(trainSelectedForBooking.getSeatMap().keySet());
                    for (int i = 0; i < seatTypes.size(); i++) {
                        System.out.println((i + 1) + ". " + seatTypes.get(i));
                    }

                    // Step 5 — Select seat type
                    System.out.println("Enter seat type (e.g. SL, 3AC, 2AC, 1AC):");
                    int seatTypeChoice = scanner.nextInt();
                    String seatType = seatTypes.get(seatTypeChoice - 1);

                    // Step 6 — Number of seats
                    System.out.println("Enter number of seats:");
                    int numberOfSeats = scanner.nextInt();

                    // Step 7 — Book
                    Boolean booked = bookingService.bookTicket(
                            loggedInUser,
                            trainSelectedForBooking,
                            bookSource,
                            bookDest,
                            seatType,
                            numberOfSeats
                    );

                    if (booked) {
                        System.out.println("Booking confirmed!");
                    } else {
                        System.out.println("Booking failed. Please try again.");
                    }
                    break;

                case 5:
                    // View bookings — login required
                    if (loggedInUser == null) {
                        System.out.println("Please login first (option 2).");
                        break;
                    }
                    List<ticket.booking.entities.Ticket> tickets = loggedInUser.getTicketsBooked();
                    if (tickets == null || tickets.isEmpty()) {
                        System.out.println("You have no bookings.");
                    } else {
                        System.out.println("Your bookings:");
                        for (int i = 0; i < tickets.size(); i++) {
                            ticket.booking.entities.Ticket t = tickets.get(i);
                            System.out.println((i + 1) + ". Ticket ID: " + t.getTicketId()
                                    + " | " + t.getSource() + " -> " + t.getDestination()
                                    + " | Seat Type: " + t.getSeatType()
                                    + " | Seats: " + t.getSeatNumbers()
                                    + " | Date: " + t.getDateOfTravel());
                        }
                    }
                    break;

                case 6:
                    // Cancel booking — login required
                    if (loggedInUser == null) {
                        System.out.println("Please login first (option 2).");
                        break;
                    }

                    List<Ticket> userTickets = loggedInUser.getTicketsBooked();
                    if (userTickets == null || userTickets.isEmpty()) {
                        System.out.println("You have no bookings to cancel.");
                        break;
                    }

                    // Show all tickets so user can pick one
                    System.out.println("Your bookings:");
                    for (int i = 0; i < userTickets.size(); i++) {
                        Ticket t = userTickets.get(i);
                        System.out.println((i + 1) + ". Ticket ID: " + t.getTicketId()
                                + " | " + t.getSource() + " -> " + t.getDestination()
                                + " | Seat Type: " + t.getSeatType()
                                + " | Seats: " + t.getSeatNumbers());
                    }

                    System.out.println("Enter ticket ID to cancel:");
                    String ticketId = scanner.next();

                    bookingService.cancelBooking(loggedInUser, ticketId);
                    break;

                case 7:
                    // Logout
                    if (loggedInUser == null) {
                        System.out.println("You are not logged in.");
                    } else {
                        loggedInUser = null;
                        trainSelectedForBooking = null;
                        System.out.println("Logged out successfully.");
                    }
                    break;

                case 8:
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
    }
}