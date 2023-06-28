package searchengine.mapping;

import searchengine.dto.data.LemmaDto;
import searchengine.model.LemmaEntity;

import java.util.List;

public interface LemmaMapper {
    LemmaDto toLemmaDto(LemmaEntity lemmaEntity);
    LemmaEntity toLemmaEntity(LemmaDto lemmaDto);
    List<LemmaDto> toListLemmaDto(List<LemmaEntity> lemmaEntityList);
    List<LemmaEntity> toListLemmaEntity(List<LemmaDto> lemmaDtoList);

}
