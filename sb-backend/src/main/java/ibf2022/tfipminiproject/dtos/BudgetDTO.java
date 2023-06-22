package ibf2022.tfipminiproject.dtos;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BudgetDTO {
    private UUID id;
    private String name;
    private BigDecimal moneyPool;
}
