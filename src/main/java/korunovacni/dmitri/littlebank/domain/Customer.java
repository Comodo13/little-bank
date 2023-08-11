package korunovacni.dmitri.littlebank.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "bank_customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String surname;
    private String sex;
    private String nationality;
    private Date dateOfBirth;
    private Long cardNumber;
    private Date cardDateOfIssue;
    private Date cardDateOfExpiry;
    @OneToMany(mappedBy = "customer")
    private List<Account> accounts = new ArrayList<>();

}
