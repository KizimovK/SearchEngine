package searchengine.mapping;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import searchengine.dto.data.PageDto;
import searchengine.model.PageEntity;

import java.util.List;
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PageMapper {
    PageEntity toPageEntity(PageDto pageDto);
    PageDto toPageDto(PageEntity pageEntity);

    List<PageEntity> toListPageEntity(List<PageDto> pageDtoList);
    List<PageDto> toListPageDto(List<PageEntity> pageEntityList);
}
