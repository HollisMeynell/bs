package l.f.mappool.exception;

@SuppressWarnings("unused")
public class NotFoundException extends HttpTipException {
    public NotFoundException() {
        super("not found!");
    }
}
