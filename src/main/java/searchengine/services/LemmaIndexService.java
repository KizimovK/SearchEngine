package searchengine.services;

import searchengine.dto.data.SiteDto;
import searchengine.model.LemmaEntity;
import searchengine.model.SiteEntity;

import java.util.List;

public interface LemmaIndexService {
//    Map<String, Integer> getLemmas(String text);

    int updateLemma(int idLemma);

    int newLemmaSave(String lemma, SiteEntity siteEntity);
    void indexSave(int idLemma, int idPage, float rank);
    List<LemmaEntity> findAllLemmas(SiteDto siteDto);


}
