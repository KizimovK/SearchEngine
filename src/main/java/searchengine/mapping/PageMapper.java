package searchengine.mapping;

import searchengine.dto.data.PageDto;
import searchengine.model.PageEntity;

import java.util.List;

//@Mapper(componentModel = "spring")
public interface PageMapper {
    PageEntity toPageEntity(PageDto pageDto);
    PageDto toPageDto(PageEntity pageEntity);

    List<PageEntity> toListPageEntity(List<PageDto> pageDtoList);
}
