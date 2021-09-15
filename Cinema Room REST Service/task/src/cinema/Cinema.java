package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Cinema {
    int totalRows;
    int totalColumns;
    int numberOfAvailableSeats;
    int currentIncome;
    int numberOfPurchasedTickets;
    private final String password = "super_secret";

    List<Seat> availableSeats;
    Map<UUID, Seat> boughtTickets;

    public Cinema(int totalColumns, int totalRows) {
        this.totalColumns = totalColumns;
        this.totalRows = totalRows;
        numberOfAvailableSeats = totalColumns * totalRows;
        currentIncome = 0;
        numberOfPurchasedTickets = 0;
        availableSeats = new ArrayList<>();
        boughtTickets = new ConcurrentHashMap<>();
        for (int i = 1; i <= totalRows; i++) {
            for (int j = 1; j <= totalColumns; j++) {
                int price = i <= 4 ? 10 : 8;
                availableSeats.add(new Seat(i, j , price));
            }
        }
    }

    public Map<UUID, Seat> buyTicket(int row, int column) {
        Optional<Seat> ticket = availableSeats.stream().filter(e -> e.row == row && e.column == column).findFirst();
        if (ticket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The ticket has been already purchased!");
        }
        availableSeats.remove(ticket.get());
        numberOfAvailableSeats--;
        numberOfPurchasedTickets++;
        UUID token = UUID.randomUUID();
        boughtTickets.put(token, ticket.get());
        currentIncome += ticket.get().price;
        return Collections.singletonMap(token, boughtTickets.get(token));
    }

    public Seat returnTicket(UUID token) {
        if (!boughtTickets.containsKey(token)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong token!");
        }
        numberOfAvailableSeats++;
        numberOfPurchasedTickets--;
        currentIncome -= boughtTickets.get(token).price;
        return boughtTickets.remove(token);
    }

    public Map<String, Integer> getStatistics(String password) {
        if (!this.password.equals(password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The password is wrong!");
        }
        return Map.of("current_income", currentIncome, "number_of_available_seats", numberOfAvailableSeats, "number_of_purchased_tickets", numberOfPurchasedTickets);
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getTotalColumns() {
        return totalColumns;
    }

    public List<Seat> getAvailableSeats() {
        return availableSeats;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public void setTotalColumns(int totalColumns) {
        this.totalColumns = totalColumns;
    }

    public void setAvailableSeats(List<Seat> availableSeats) {
        this.availableSeats = availableSeats;
    }

}








