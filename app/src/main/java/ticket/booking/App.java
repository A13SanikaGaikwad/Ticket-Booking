package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.service.UserBookingService;
import ticket.booking.util.UserServiceUtil;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class App {

    public static void main(String[] args) {
        System.out.println("Running Train Booking System");
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        UserBookingService userBookingService;
        User currentUser = null;
        Train trainSelectedForBooking = null;

        try {
            userBookingService = new UserBookingService(); // default instance, possibly for signup
        } catch (IOException ex) {
            System.out.println("There is something wrong");
            return;
        }

        while (option != 7) {
            System.out.println("\nChoose option");
            System.out.println("1. Sign up");
            System.out.println("2. Login");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel my Booking");
            System.out.println("7. Exit the App");

            option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.println("Enter the username to signup");
                    String nameToSignUp = scanner.next();
                    System.out.println("Enter the password to signup");
                    String passwordToSignUp = scanner.next();
                    User userToSignup = new User(nameToSignUp, passwordToSignUp,
                            UserServiceUtil.hashPassword(passwordToSignUp),
                            new ArrayList<>(), UUID.randomUUID().toString());
                    userBookingService.signUp(userToSignup);
                    break;

                case 2:
                    System.out.println("Enter the username to Login");
                    String nameToLogin = scanner.next();
                    System.out.println("Enter the password to login");
                    String passwordToLogin = scanner.next();
                    User userToLogin = new User(nameToLogin, passwordToLogin,
                            UserServiceUtil.hashPassword(passwordToLogin),
                            new ArrayList<>(), UUID.randomUUID().toString());
                    try {
                        userBookingService = new UserBookingService(userToLogin);
                        currentUser = userToLogin;
                        System.out.println("Login successful!");
                    } catch (IOException ex) {
                        System.out.println("Login failed.");
                    }
                    break;

                case 3:
                    if (currentUser == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    System.out.println("Fetching your bookings");
                    userBookingService.fetchBookings();
                    break;

                case 4:
                    if (currentUser == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    System.out.println("Type your source station");
                    String source = scanner.next();
                    System.out.println("Type your destination station");
                    String destination = scanner.next();
                    List<Train> trains = userBookingService.getTrains(source, destination);
                    if (trains.isEmpty()) {
                        System.out.println("No trains found.");
                        break;
                    }
                    int index = 1;
                    for (Train t : trains) {
                        System.out.println(index + ". Train ID: " + t.getTrainId());
                        for (Map.Entry<String, String> entry : t.getStationTimes().entrySet()) {
                            System.out.println("Station: " + entry.getKey() + ", Time: " + entry.getValue());
                        }
                        index++;
                    }
                    System.out.println("Select a train by typing 1, 2, 3...");
                    int trainChoice = scanner.nextInt();
                    if (trainChoice <= 0 || trainChoice > trains.size()) {
                        System.out.println("Invalid choice.");
                    } else {
                        trainSelectedForBooking = trains.get(trainChoice - 1);
                    }
                    break;

                case 5:
                    if (currentUser == null) {
                        System.out.println("Please login first.");
                        break;
                    }
                    if (trainSelectedForBooking == null) {
                        System.out.println("Please search and select a train first.");
                        break;
                    }
                    System.out.println("Available seats:");
                    List<List<Integer>> seats = userBookingService.fetchSeats(trainSelectedForBooking);
                    for (List<Integer> row : seats) {
                        for (Integer val : row) {
                            System.out.print(val + " ");
                        }
                        System.out.println();
                    }
                    System.out.println("Enter the row");
                    int row = scanner.nextInt();
                    System.out.println("Enter the column");
                    int col = scanner.nextInt();
                    System.out.println("Booking your seat....");
                    Boolean booked = userBookingService.bookTrainSeat(trainSelectedForBooking, row, col);
                    if (booked.equals(Boolean.TRUE)) {
                        System.out.println("Booked! Enjoy your journey.");
                    } else {
                        System.out.println("Can't book this seat. It may be already booked or invalid.");
                    }
                    break;

                case 6:
                    System.out.println("Cancel feature is not yet implemented.");
                    break;

                case 7:
                    System.out.println("Exiting app. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }

    }
}
