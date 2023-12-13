package l.f.mappool.entity.osu;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "account_user")
public class OsuAccountUser {
    @Id
    Long uid;

    @Column(columnDefinition = "text")
    private String username;

    @Column(columnDefinition = "text")
    private String password;

    @Column(columnDefinition = "text")
    private String session;

    @Column(columnDefinition = "text")
    private String token;
}
