package propensi.pmosystem.model;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import propensi.pmosystem.model.UserModel;

@Setter
@Getter
@Entity
@Table(name = "project_user")
public class ProjectUserModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "created_by")
    private long created_by;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime created_at;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private ProjectModel project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private UserModel user;
}

