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

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance")
public class AttendanceModel implements Serializable {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private EventModel event;

    @Size(max = 1000)
    @Column(name = "name")
    @NotNull
    private String name;

    @Size(max = 1000)
    @Column(name = "cp")
    @NotNull
    private String cp;

    @Size(max = 1000)
    @Column(name = "email")
    @NotNull
    private String email;

    @Size(max = 1000)
    @Column(name = "department")
    @NotNull
    private String department;

    @Size(max = 1000)
    @Column(name = "role")
    @NotNull
    private String role;

    @Column(name = "created_by")
    private Long created_by;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime created_at;
}
