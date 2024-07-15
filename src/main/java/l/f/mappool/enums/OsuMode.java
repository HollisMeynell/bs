package l.f.mappool.enums;

import lombok.Getter;

@Getter
public enum OsuMode {
    OSU("osu",0),
    TAIKO("taiko",1),
    CATCH("fruits",2),
    MANIA("mania",3),
    DEFAULT("",-1);

    final String name;
    final short  value;

    OsuMode(String mode, int i) {
        name = mode;
        value = (short) i;
    }

    public static OsuMode getMode(String desc){
        if (desc == null) return DEFAULT;
        return switch (desc.toLowerCase()) {
            case "osu", "o", "0" -> OSU;
            case "taiko", "t", "1" -> TAIKO;
            case "catch", "c", "fruits", "f", "2" -> CATCH;
            case "mania", "m", "3" -> MANIA;
            default -> DEFAULT;
        };
    }
    public static OsuMode getMode(int desc){
        return switch (desc) {
            case 0 -> OSU;
            case 1 -> TAIKO;
            case 2 -> CATCH;
            case 3 -> MANIA;
            default -> DEFAULT;
        };
    }

}
