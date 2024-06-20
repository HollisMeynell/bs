package l.f.mappool.entity.file;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@DynamicUpdate
@Table(name = "files", indexes = {
        @Index(name = "local_name", columnList = "local")
})
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class FileRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(name = "local", columnDefinition = "text")
    String localName;

    @Column(name = "name", columnDefinition = "text")
    String fileName;

    @Column(name = "create_from", columnDefinition = "TIMESTAMP")
    LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "update_from", columnDefinition = "TIMESTAMP")
    LocalDateTime updateTime = LocalDateTime.now();

    public void update() {
        this.updateTime = LocalDateTime.now();
    }
}
