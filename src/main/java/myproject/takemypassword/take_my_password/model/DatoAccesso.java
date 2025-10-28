package myproject.takemypassword.take_my_password.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "dati")
public class DatoAccesso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
 
    @NotBlank(message = "Inserisci la piattaforma")
    private String platform;

    @NotBlank(message = "Inserisci un username!")
    private String username;

    @NotBlank(message = "Inserisci una password!")
    private String password;

    @Lob
    private String annotation;

    // Getter e Setter


    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }


    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAnnotation() {
        return this.annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

}
