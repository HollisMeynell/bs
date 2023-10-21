package l.f.mappool.exception;

public class PermissionException extends HttpTipException {
    public PermissionException() {
        super("Unauthorized");
        code = 401;
    }

    public PermissionException(String message) {
        super(message);
        code = 401;
    }
}
