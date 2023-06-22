package ibf2022.tfipminiproject.mappers;

import org.mapstruct.Mapper;

import ibf2022.tfipminiproject.dtos.BudgetDTO;
import ibf2022.tfipminiproject.entities.Budget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    BudgetDTO budgetToBudgetDTO(Budget budget);
    Budget budgetDTOToBudget(BudgetDTO budgetDTO);
}
