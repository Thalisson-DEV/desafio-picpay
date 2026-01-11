package com.desafiopicpay.domain.user;

import com.desafiopicpay.domain.role.Role;
import com.desafiopicpay.exceptions.InvalidOperationException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity(name = "users")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String document;
    @Column(unique = true)
    private String email;

    private String password;
    private BigDecimal balance;
    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    public void validateTransaction(@Valid User sender, BigDecimal amount) {
        if (sender.getUserType().equals(UserType.MERCHANT)) {
            throw new InvalidOperationException("User unauthorized");
        }
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InvalidOperationException("Insufficient balance");
        }
    }
}
