package l.f.mappool.exception;

public class PermissionException extends RuntimeException{
    public PermissionException() {
        super("You are not allow!");
    }
}
