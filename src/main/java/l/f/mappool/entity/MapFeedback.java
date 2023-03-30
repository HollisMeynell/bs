package l.f.mappool.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;


@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Entity
@DynamicUpdate
@Table(name = "pool_feedback")
public class MapFeedback {

    @Id
    FeedbackId id;
    @Column(name = "deleted", columnDefinition = "boolean default false")
    boolean deleted;

    @Column(name = "feedback", columnDefinition = "text")
    String feedback;

    Boolean agree;
}

