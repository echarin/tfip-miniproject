package ibf2022.tfipminiproject.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDTO {
    private UUID id;
    private String name;
    private BigDecimal budgetedAmount;
    private LocalDateTime createdAt;
}
