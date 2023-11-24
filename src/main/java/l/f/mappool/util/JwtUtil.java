package l.f.mappool.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import l.f.mappool.entity.LoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class JwtUtil {
    private static Logger log = LoggerFactory.getLogger(JwtUtil.class);
    private static final String SECRET = "6*x?ata%sd6";
    private static final Algorithm ALGORITHM = Algorithm.HMAC512(SECRET);
    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();
    private static final Set<Long> ADMIN_USER_SET = new HashSet<>();

    public static void setAdminUsers(Collection<Long> users) {
        ADMIN_USER_SET.addAll(users);
    }

    public static String createToken(LoginUser loginUser) {
        return JWT
                .create()
                .withClaim("addr", loginUser.getAddr())
                .withClaim("uid", loginUser.getOsuId())
                .withClaim("code", loginUser.getCode())
                .withIssuedAt(new Date())
                .sign(ALGORITHM);
    }

    public static LoginUser verifyToken(String token) {
        try {
            DecodedJWT jwt = VERIFIER.verify(token);
            var user = new LoginUser();
            user.setAddr(jwt.getClaim("addr").asString());
            user.setCode(jwt.getClaim("code").asString());
            user.setOsuId(jwt.getClaim("uid").asLong());
            if (ADMIN_USER_SET.contains(user.getOsuId())) user.setAdmin(true);
            ContextUtil.setContextUser(user);
            return user;
        } catch (Exception e) {
            log.error("token解码异常", e);
        }
        return null;
    }

}
