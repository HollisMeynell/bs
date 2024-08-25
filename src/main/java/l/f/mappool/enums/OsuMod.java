package l.f.mappool.enums;

import l.f.mappool.exception.ModsCatchException;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public enum OsuMod {
    None(0, "NM"),
    NoFail(1, "NF"),
    Easy(1 << 1, "EZ"),
    //替换未使用的 No Video
    TouchDevice(1 << 2, "TD"),
    Hidden(1 << 3, "HD"),
    HardRock(1 << 4, "HR"),
    SuddenDeath(1 << 5, "SD"),
    DoubleTime(1 << 6, "DT"),
    Relax(1 << 7, "RL"),
    HalfTime(1 << 8, "HT"),
    //总是和 DT 一起使用 : 512 + 64 = 576
    Nightcore((1 << 9) | (1 << 6), "NC"),
    Flashlight(1 << 10, "FL"),
    Autoplay(1 << 11, "AT"),
    SpunOut(1 << 12, "SO"),
    //Autopilot
    Autopilot(1 << 13, "AP"),
    Perfect(1 << 14, "PF"),
    Key4(1 << 15, "4K"),
    Key5(1 << 16, "5K"),
    Key6(1 << 17, "6K"),
    Key7(1 << 18, "7K"),
    Key8(1 << 19, "8K"),
    FadeIn(1 << 20, "FI"),
    // mania rd
    Random(1 << 21, "RD"),
    //Cinema
    Cinema(1 << 22, "CM"),
    //仅 osu!cuttingedge
    TargetPractice(1 << 23, "TP"),
    Key9(1 << 24, "9K"),
    KeyCoop(1 << 25, "CP"),
    Key1(1 << 26, "1K"),
    Key3(1 << 27, "3K"),
    Key2(1 << 28, "2K"),
    ScoreV2(1 << 29, "V2"),
    Mirror(1 << 30, "MR"),
    //    keyMod(Key1.value | Key2.value | Key3.value | Key4.value | Key5.value | Key6.value | Key7.value | Key8.value | Key9.value | KeyCoop.value),
    keyMod(521109504, "?"),
    //    FreeModAllowed(NoFail.value | Easy.value | Hidden.value | HardRock.value | SuddenDeath.value | Flashlight.value | FadeIn.value | Relax.value | Relax2.value | SpunOut.value | keyMod.value),
    FreeModAllowed(522171579, "FM"),
    //    ScoreIncreaseMods(Hidden.value | HardRock.value | Flashlight.value | DoubleTime.value | FadeIn.value)
    ScoreIncreaseMods(1049688, "IM"),
    // 其他未上传的mod
    Other(0, "");
    public final int value;
    public final String abbreviation;

    OsuMod(int i, String name) {
        value = i;
        abbreviation = name;
    }

    public static List<OsuMod> getModsList(String modsStr) {
        var modStrArray = getModsString(modsStr);
        var mList = Arrays.stream(modStrArray).map(OsuMod::fromStr).filter(e -> e != Other).toList();
        check(mList);
        return mList;
    }

    public static int getModsValue(String modsStr) {
        if (!StringUtils.hasText(modsStr)) return 0;
        var modStrArray = getModsString(modsStr);
        var mList = Arrays.stream(modStrArray).map(OsuMod::fromStr).filter(e -> e != Other).toList();
        return getModsValue(mList);
    }

    private static String[] getModsString(String modsStr) {
        var newStr = modsStr.replaceAll("\\s+", "");
        if (newStr.length() % 2 != 0) {
            throw ModsCatchException.Create.SiseException();
        }
        return newStr.split("(?<=\\w)(?=(\\w{2})+$)");
    }

    private static void check(List<OsuMod> osuModList) {
        if (osuModList.contains(None) && osuModList.size() > 1) {
            throw ModsCatchException.Create.ConflictException(None);
        }
        int modValue = osuModList.stream().map(m -> m.value).reduce(0, (i, s) -> s | i);

        if ((modValue & 320) == 320) {
            throw ModsCatchException.Create.ConflictException(DoubleTime, HalfTime);
        }

        if ((modValue & 18) == 18) {
            throw ModsCatchException.Create.ConflictException(HardRock, Easy);
        }
        if ((modValue & 1) != 0 && (modValue & 16416) != 0) {
            throw ModsCatchException.Create.ConflictException(NoFail, SuddenDeath, Perfect);
        }
    }

    public static int getModsValueFromStr(List<String> mList) {
        return getModsValue(mList.stream().map(OsuMod::fromStr).toList());
    }

    public static int getModsValue(List<OsuMod> mList) {
        check(mList);
        return mList.stream().map(m -> m.value).reduce(0, (i, s) -> s | i);
    }

    public static OsuMod fromStr(String modStr) {
        return switch (modStr.toUpperCase()) {
            case "NM" -> None;
            case "NF" -> NoFail;
            case "EZ" -> Easy;
            case "HT" -> HalfTime;
            case "TD" -> TouchDevice;
            case "HR" -> HardRock;
            case "HD" -> Hidden;
            case "FI" -> FadeIn;
            case "SD" -> SuddenDeath;
            case "PF" -> Perfect;
            case "DT" -> DoubleTime;
            case "NC" -> Nightcore;
            case "FL" -> Flashlight;
            case "RL" -> Relax;
            case "AP" -> Autopilot;
            case "AT" -> Autoplay;
            case "CM" -> Cinema;
            case "SO" -> SpunOut;
            case "CP" -> KeyCoop;
            case "MR" -> Mirror;
            case "RD" -> Random;
            case "SV2" -> ScoreV2;
            default -> Other;
        };
    }

    private static final int changeRatingValue = Easy.value | HalfTime.value | TouchDevice.value |
            HardRock.value | DoubleTime.value | Nightcore.value | Flashlight.value;

    public static boolean hasChangeRating(int i) {
        return (changeRatingValue & i) != 0;
    }

    private static final int changePPValue = NoFail.value | Easy.value | HalfTime.value | TouchDevice.value |
            HardRock.value | DoubleTime.value | Nightcore.value | Hidden.value | Flashlight.value |
            SpunOut.value;

    public static boolean hasChangePP(int i) {
        return (ScoreIncreaseMods.value & i) != 0;
    }

    public boolean check(int i) {
        return (value & i) != 0;
    }

    public static boolean hasDt(int i) {
        return ((DoubleTime.value | Nightcore.value) & i) != 0;
    }

    public static boolean hasHt(int i) {
        return HalfTime.check(i);
    }

    public static boolean hasHr(int i) {
        return HardRock.check(i);
    }

    public static boolean hasEz(int i) {
        return Easy.check(i);
    }

    public static int add(int old, OsuMod osuMod) {
        return old | osuMod.value;
    }

    public int add(int old) {
        return old | this.value;
    }
}
