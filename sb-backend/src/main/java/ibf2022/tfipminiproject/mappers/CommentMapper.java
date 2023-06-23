package ibf2022.tfipminiproject.mappers;

import org.mapstruct.Mapper;

import ibf2022.tfipminiproject.dtos.CommentDTO;
import ibf2022.tfipminiproject.entities.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDTO commentToCommentDTO(Comment comment);
    Comment commentDTOToComment(CommentDTO commentDTO);
}
