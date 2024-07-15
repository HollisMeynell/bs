package l.f.mappool.exception;

import l.f.mappool.enums.OsuMod;

import java.util.Arrays;

public class ModsCatchException extends RuntimeException{
    public static class Create{
        public static ModsCatchException SiseException(){
            // +NFL 字符数量数量不对
            return new ModsCatchException("mod 输入异常: 输入错误.");
        }
        public static ModsCatchException ConflictException(OsuMod... osuMods){
            // +NM&任意mod or EZ&HR DT&HT NF&PF&SD
            return new ModsCatchException("mod 输入异常: 冲突的 mod"+ Arrays.toString(osuMods));
        }
    }

    public ModsCatchException(String msg){
        super(msg);
    }
}
