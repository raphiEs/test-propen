package propensi.pmosystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Setter
@Getter
@Entity
@Table(name = "company")
public class CompanyModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Size(max = 256)
    @Column(name = "location", nullable = false)
    private String location;


    @Size(max = 1000)
    @Column(name = "logo")
    private String logo;

    @Column(name = "created_by")
    private long created_by;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime created_at;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private BusinessModel business;

    @OneToMany(mappedBy = "name", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CompanyModel> projectCompany;
}
