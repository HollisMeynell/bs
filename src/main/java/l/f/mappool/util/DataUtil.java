package l.f.mappool.util;

import l.f.mappool.enums.Mod;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataUtil {
    private static int AR2MS(float ar){
        if (0 < ar - 5){
            if (ar > 11) return 300;
            return  1200 - (int) (150 * (ar - 5));
        } else {
            return  1800 - (int) (120 * ar);
        }
    }

    private static float MS2AR(int ms){
        if (0 < 1200 - ms){
            if (ms < 300) return 11;
            return  5 + (1200 - ms) / 150f;
        } else {
            if (ms >= 2400) return -5;
            return  (1800 - ms) / 120f;
        }
    }

    public static float AR(float ar, int mod){
        int ms;
//      1800  -  1200  -  450  -  300
        if (Mod.hasHr(mod)){
            ar *= 1.4;
        } else if (Mod.hasEz(mod)) {
            ar /= 2;
        }
        ms = AR2MS(ar);
        if (Mod.hasDt(mod)){
            ms /= (3d/2);
        } else if (Mod.hasHt(mod)) {
            ms /= (3d/4);
        }
        ar = MS2AR(ms);
        return (int)Math.ceil(ar * 100)/100f;
    }

    private static float OD2MS(float od){
        if (od > 10) return 20;
        return 80 - (6 * od);
    }

    private static float MS2OD(float ms){
        return (80 - ms) / 6f;
    }

    public static float OD(float od, int mod){
        float ms;
        if (Mod.hasHr(mod)){
            od *= 1.4f;
        } else if (Mod.hasEz(mod)) {
            od /= 2f;
        }
        ms = OD2MS(od);

        if (Mod.hasDt(mod)){
            ms /= (3d/2);
        } else if (Mod.hasHt(mod)) {
            ms /= (3d/4);
        }
        return (int)Math.ceil(MS2OD(ms)*100) / 100f;
    }


    public static float CS(float cs, int mod){
        if (Mod.hasHr(mod)){
            cs *= 1.3f;
        } else if (Mod.hasEz(mod)) {
            cs /= 2f;
        }
        return cs;
    }
    public static float HP(float hp, int mod){
        if (Mod.hasHr(mod)){
            hp *= 1.3f;
        } else if (Mod.hasEz(mod)) {
            hp /= 1.3f;
        }
        return hp;
    }

    @NotNull
    public static String getFileMd5(Path file) {
        if (!Files.exists(file)) {
            return "";
        }
        try {
            long size = Files.size(file);
            if (size < 512000) {
                var all = Files.readAllBytes(file);
                return DigestUtils.md5DigestAsHex(all);
            }

            try (var input = Files.newInputStream(file)) {
                return DigestUtils.md5DigestAsHex(input);
            }
        } catch (IOException e) {
            return "";
        }
    }

}
