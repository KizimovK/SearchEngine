package searchengine.mapping;

import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.LemmaEntity;

import java.util.HashMap;
import java.util.List;

public interface LemmaMapper {
    LemmaDto toLemmaDto(LemmaEntity lemmaEntity);
    LemmaEntity toLemmaEntity(LemmaDto lemmaDto);
    List<LemmaDto> toListLemmaDto(List<LemmaEntity> lemmaEntityList);
    List<LemmaEntity> toListLemmaEntity(List<LemmaDto> lemmaDtoList);
    List<LemmaDto> toListLemmaDto(HashMap<String, Integer> mapLemmas, SiteDto siteDto);
    List<LemmaEntity> toListLemmaEntity(HashMap<String, Integer> mapLemmas, SiteDto siteDto);

}
