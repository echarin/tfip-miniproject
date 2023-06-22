package ibf2022.tfipminiproject.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ibf2022.tfipminiproject.dtos.BudgetDTO;
import ibf2022.tfipminiproject.entities.Budget;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    BudgetDTO budgetToBudgetDTO(Budget budget);
    
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "removeCategory", ignore = true)
    Budget budgetDTOToBudget(BudgetDTO budgetDTO);
}
