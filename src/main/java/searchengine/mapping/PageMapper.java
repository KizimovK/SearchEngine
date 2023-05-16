package searchengine.mapping;

import org.mapstruct.Mapper;
import searchengine.dto.data.PageDto;
import searchengine.model.Page;

import java.util.List;

//@Mapper(componentModel = "spring")
public interface PageMapper {
    Page mapperToPage(PageDto pageDto);

    List<Page> mappingToListPage(List<PageDto> pageDtoList);
}
