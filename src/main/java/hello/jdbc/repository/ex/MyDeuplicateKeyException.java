package hello.jdbc.repository.ex;

public class MyDeuplicateKeyException extends MyDbException{
    public MyDeuplicateKeyException() {
    }

    public MyDeuplicateKeyException(String message) {
        super(message);
    }

    public MyDeuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDeuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
