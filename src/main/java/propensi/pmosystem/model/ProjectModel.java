package propensi.pmosystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "project")
public class ProjectModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "name", nullable = false)
    private String name;

    @DateTimeFormat(pattern ="yyyy-MM-dd")
    @Column(name = "start_date")
    private LocalDateTime start_date;

    @DateTimeFormat(pattern ="yyyy-MM-dd")
    @Column(name = "end_date")
    private LocalDateTime end_date;


    @Size(max = 256)
    @Column(name = "status", nullable = false)
    private String status;


    @Size(max = 1000)
    @Column(name = "internal_drive")
    private String internal_drive;

    @Size(max = 1000)
    @Column(name = "external_drive")
    private String external_drive;

    @Column(name = "created_by")
    private long created_by;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime created_at;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private CompanyModel company;
}

