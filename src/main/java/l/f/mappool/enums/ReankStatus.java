package l.f.mappool.enums;

public enum ReankStatus {
    graveyard(-2),
    wip(-1),
    pending(0),
    ranked(1),
    approved(2),
    qualified(3),
    loved(4),
    unknown(5);
    final int value;
    ReankStatus(int i) {
        value = i;
    }
    public static ReankStatus fromInteger(int i) {
        return switch (i){
            case -2 -> graveyard;
            case -1 -> wip;
            case -0 -> pending;
            case 1 -> ranked;
            case 2 -> approved;
            case 3 -> qualified;
            case 4 -> loved;
            default -> unknown;
        };
    }
    public int getStatusInt(){
        return value;
    }
}
