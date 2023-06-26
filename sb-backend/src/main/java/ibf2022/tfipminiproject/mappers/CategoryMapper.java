package ibf2022.tfipminiproject.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ibf2022.tfipminiproject.dtos.CategoryDTO;
import ibf2022.tfipminiproject.sqlentities.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO categoryToCategoryDTO(Category category);

    @Mapping(target = "budget", ignore = true)
    @Mapping(target = "expenses", ignore = true)
    @Mapping(target = "removeExpense", ignore = true)
    Category categoryDTOToCategory(CategoryDTO categoryDTO);
}
