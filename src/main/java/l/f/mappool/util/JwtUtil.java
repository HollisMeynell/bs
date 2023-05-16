package l.f.mappool.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import l.f.mappool.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JwtUtil {
    private static Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private static final String SECRET = "6*x?ata%sd6";
    private static final Algorithm ALGORITHM = Algorithm.HMAC512(SECRET);
    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();

    public static String createToken(User user) {
        return JWT
                .create()
                .withClaim("addr", user.getAddr())
                .withClaim("uid", user.getOsuId())
                .withClaim("code", user.getCode())
                .withIssuedAt(new Date())
                .sign(ALGORITHM);
    }

    public static User verifyToken(String token) {
        try {
            DecodedJWT jwt = VERIFIER.verify(token);
            var user = new User();
            user.setAddr(jwt.getClaim("addr").asString());
            user.setCode(jwt.getClaim("code").asString());
            user.setOsuId(jwt.getClaim("uid").asLong());

            return user;
        } catch (Exception e) {
            log.error("token解码异常", e);
        }
        return null;
    }

}
