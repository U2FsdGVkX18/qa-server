package qa.common;

public class CustomerException extends RuntimeException {
    private String retMessage;

    public CustomerException() {
        super();
    }

    public CustomerException(String retMessage) {
        this.retMessage = retMessage;
    }

    public String getRetMessage() {
        return retMessage;
    }

}
