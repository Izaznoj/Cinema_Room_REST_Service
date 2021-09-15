package cinema;

import java.util.UUID;

class ReturnForm {
    UUID token;

    ReturnForm(){}

    ReturnForm(UUID token) {
        this.token = token;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }
}
