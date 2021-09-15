package cinema;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
public class CinemaController {
    Cinema cinema = new Cinema(9, 9);

    @GetMapping("/seats")
    public Cinema getSeats() {
        return cinema;
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseSeat(@RequestBody PurchaseForm form){
        if (form.getRow() < 1 || form.getRow() > 9 || form.getColumn() < 1 || form.getColumn() > 9) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "The number of a row or a column is out of bounds!"));
        }
        try {
            Map<UUID, Seat> ticket = cinema.buyTicket(form.getRow(), form.getColumn());
            UUID token = ticket.keySet().iterator().next();
            return ResponseEntity.ok().body(Map.of("ticket", ticket.get(token), "token", token ));
        }
        catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnTicket(@RequestBody ReturnForm form) {
        try {
            Seat returnedTicket = cinema.returnTicket(form.token);
            return ResponseEntity.ok().body(Collections.singletonMap("returned_ticket", returnedTicket));
        } catch (ResponseStatusException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getReason()));
        }
    }

    @PostMapping("/stats")
    public ResponseEntity<?> getStatistics(@RequestParam(required = false) String password) {
        try {
            Map<String, Integer> statistics = cinema.getStatistics(Optional.ofNullable(password)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "The password is wrong!")));
            return ResponseEntity.ok().body(statistics);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getReason()));
        }
    }
}
