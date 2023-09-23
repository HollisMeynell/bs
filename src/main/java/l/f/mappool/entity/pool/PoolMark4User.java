package l.f.mappool.entity.pool;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户标记常用
 */
@Getter
@Setter
@Entity
@Table(name = "map_pool_mark_4_user")
public class PoolMark4User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    long uid;
    int pid;
}