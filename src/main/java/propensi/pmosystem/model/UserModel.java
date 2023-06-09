package propensi.pmosystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import propensi.pmosystem.model.RoleModel;

@Setter
@Getter
@Entity
@Table(name = "user")
public class UserModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 256)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotNull
    @Lob
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Size(max = 256)
    @Column(name = "name", nullable = false)
    private String name;


    @Size(max = 256)
    @Column(name = "email")
    private String email;


    @Size(max = 256)
    @Column(name = "contact")
    private String contact;

    @Column(name = "created_by")
    private Long created_by;

    @DateTimeFormat(pattern ="yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime created_at;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private RoleModel role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<CompanyUserModel> companyUser;
}

