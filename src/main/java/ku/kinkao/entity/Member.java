package ku.kinkao.entity;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import ku.kinkao.config.AttributeEncryptor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class Member {

    @Id
    @GeneratedValue
    private UUID id;

    @Convert(converter = AttributeEncryptor.class)
    private String username;

    private String password;

    @Convert(converter = AttributeEncryptor.class)
    private String firstName;

    @Convert(converter = AttributeEncryptor.class)
    private String lastName;

    @Convert(converter = AttributeEncryptor.class)
    private String email;

    private String role;

    private Instant createdAt;
}
