package l.f.mappool.exception;

public class PermissionException extends LogException{
    public PermissionException() {
        super("You are not allow!");
        code = 401;
    }

    public PermissionException(String message) {
        super(message);
        code = 401;
    }
}
