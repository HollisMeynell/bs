package l.f.mappool.entity.file;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public void update() {
        this.updateTime = LocalDateTime.now();
    }
}
