package botservice.serviceException;

/**
 * Объект для описания ошибок
 */

public class ServiceExceptionObject {

    private Throwable throwable;
    private String message;

    public ServiceExceptionObject(String message, Throwable throwable){
        this.throwable = throwable;
        this.message = message;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public String getMessage() {
        return message;
    }
}
