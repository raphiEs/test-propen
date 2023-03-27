package propensi.pmosystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Setter@Getter@Entity@NoArgsConstructor@AllArgsConstructor
@Table(name = "event")
public class EventModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private ProjectModel project;

    @Size(max = 1000)
    @Column(name = "name")
    @NotNull
    private String name;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_date")
    @NotNull
    private LocalDateTime startDate;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Size(max = 1000)
    @Column(name = "summary")
    private String summary;

    @Size()
    @Column(name = "detailed_summary")
    private String detailedSummary;

    @Size(max = 1000)
    @Column(name = "mom_url")
    private String momUrl;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "mom_last_update")
    private LocalDateTime momLastUpdate;

    @Column(name = "created_by")
    private Long created_by;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AttendanceModel> eventAttendance;
}
