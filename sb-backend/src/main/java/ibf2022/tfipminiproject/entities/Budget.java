package ibf2022.tfipminiproject.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, scale = 2)
    private Double moneyPool;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CategoryGroup> categoryGroups = new ArrayList<>();

    // Helper methods
    public Budget addCategoryGroup(CategoryGroup categoryGroup) {
        this.categoryGroups.add(categoryGroup);
        categoryGroup.setBudget(this);
        return this;
    }

    public Budget removeCategoryGroup(CategoryGroup categoryGroup) {
        this.categoryGroups.remove(categoryGroup);
        categoryGroup.setBudget(null);
        return this;
    }
}
