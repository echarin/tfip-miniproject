package ibf2022.tfipminiproject.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ibf2022.tfipminiproject.dtos.ExpenseDTO;
import ibf2022.tfipminiproject.sqlentities.Expense;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(source = "category.name", target = "categoryName")
    ExpenseDTO expenseToExpenseDTO(Expense expense);

    @Mapping(target = "category", ignore = true)
    Expense expenseDTOToExpense(ExpenseDTO expenseDTO);
}
