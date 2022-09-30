package gr.scify.icsee.data.model;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class LoggedInUser {

    private final String email;
    private final String auth_token;

    public LoggedInUser(String email, String auth_token) {
        this.email = email;
        this.auth_token = auth_token;
    }

    public String getEmail() {
        return email;
    }

    public String getAuthToken() {
        return auth_token;
    }
}