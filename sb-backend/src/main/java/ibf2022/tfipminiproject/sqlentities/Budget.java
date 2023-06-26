package ibf2022.tfipminiproject.sqlentities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Budget extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, scale = 2)
    private BigDecimal moneyPool;

    @ToString.Exclude
    @OneToOne(mappedBy = "budget", fetch = FetchType.LAZY)
    private User user;

    @ToString.Exclude
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Category> categories = new ArrayList<>();

    // Helper methods
    public Budget addCategory(Category category) {
        this.categories.add(category);
        category.setBudget(this);
        return this;
    }

    public Budget removeCategory(Category category) {
        this.categories.remove(category);
        category.setBudget(null);
        return this;
    }
}
