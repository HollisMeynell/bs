package l.f.mappool.entity;


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

    @Column(columnDefinition = "TEXT")
    private String password;

    @Column(columnDefinition = "TEXT")
    private String session;

    @Column(columnDefinition = "TEXT")
    private String token;
}
