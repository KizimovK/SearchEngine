package searchengine.mapping;

import searchengine.dto.data.IndexDto;
import searchengine.model.IndexEntity;

import java.util.HashMap;
import java.util.List;

public interface IndexMapper {
    IndexEntity toIndexEntity(IndexDto indexDto);
    IndexDto toIndexDto(IndexEntity indexEntity);
    List<IndexEntity> toListIndexEntity(List<IndexDto> indexDtoList);
    List<IndexDto> toListIndexDto(List<IndexEntity> indexEntityList);

}
