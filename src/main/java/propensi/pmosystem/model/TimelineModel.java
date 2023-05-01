package propensi.pmosystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@Setter
@Getter
@Entity
@Table(name = "timeline")
public class TimelineModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private ProjectModel project;

    @Size(max = 1000)
    @Column(name = "milestone_name")
    private String milestone_name;

    @Column(name = "weight")
    private Integer weight;
    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_date")
    @NotNull
    private LocalDateTime startDate;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Size(max = 256)
    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_by")
    private Long created_by;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime created_at;
}
