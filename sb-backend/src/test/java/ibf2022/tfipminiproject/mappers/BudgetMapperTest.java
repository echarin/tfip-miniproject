package ibf2022.tfipminiproject.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ibf2022.tfipminiproject.dtos.BudgetDTO;
import ibf2022.tfipminiproject.entities.Budget;

@SpringBootTest
public class BudgetMapperTest {
    
    @Autowired
    private BudgetMapper budgetMapper;

    @Test
    public void testBudgetToBudgetDTO() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Budget budget = new Budget();
        budget.setId(id);
        budget.setName("Test Budget");
        budget.setMoneyPool(new BigDecimal("1000.00"));
        budget.setCreatedAt(now);

        BudgetDTO budgetDTO = budgetMapper.budgetToBudgetDTO(budget);

        assertEquals(id, budgetDTO.getId());
        assertEquals("Test Budget", budgetDTO.getName());
        assertEquals(new BigDecimal("1000.00"), budgetDTO.getMoneyPool());
        assertEquals(now, budgetDTO.getCreatedAt());
    }

    @Test
    public void testBudgetDTOToBudget() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        BudgetDTO budgetDTO = new BudgetDTO(id, "Test Budget", new BigDecimal("1000.00"), now);

        Budget budget = budgetMapper.budgetDTOToBudget(budgetDTO);

        assertEquals(id, budget.getId());
        assertEquals("Test Budget", budget.getName());
        assertEquals(new BigDecimal("1000.00"), budget.getMoneyPool());
        assertEquals(now, budget.getCreatedAt());
    }
}
