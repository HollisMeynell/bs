package l.f.mappool.exception;

@SuppressWarnings("unused")
public class NotFoundException extends RuntimeException{
    public NotFoundException() {
        super("not found!");
    }
}
