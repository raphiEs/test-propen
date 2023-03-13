package propensi.pmosystem.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Setter 
@Getter
@Entity
@Table(name = "business")
public class BusinessModel implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "name", fetch = FetchType.LAZY)
    private List<CompanyModel> companyBusiness;
}
