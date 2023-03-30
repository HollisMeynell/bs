package l.f.mappool.enums;

public enum ReankStatus {
    graveyard,
    wip,
    pending,
    ranked,
    approved,
    qualified,
    loved,
    unknown;
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
}
