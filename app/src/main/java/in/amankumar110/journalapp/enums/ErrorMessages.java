package in.amankumar110.journalapp.enums;

public enum ErrorMessages {
    INCORRECT_EMAIL_OR_PASSWORD("Incorrect Email Or Password"),
    EMAIL_ALREADY_IN_USE("Email Is Already In Use"),
    NO_USER_FOUND("No user found with this email address."),
    SIGNUP_ERORR("Couldn't Sign Up, Try Again");


    private final String message;

    ErrorMessages(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}