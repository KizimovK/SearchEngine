package searchengine.companets;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.SiteMapper;
import searchengine.model.LemmaEntity;
import searchengine.model.SiteEntity;
import searchengine.services.LemmaIndexService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MakerIndex {
    private final ExtractLemma extractLemma;
    private final LemmaIndexService lemmaIndexService;
    private final SiteMapper siteMapper;

    public MakerIndex(ExtractLemma extractLemma, LemmaIndexService lemmaIndexService, SiteMapper siteMapper) {
        this.extractLemma = extractLemma;
        this.lemmaIndexService = lemmaIndexService;
        this.siteMapper = siteMapper;
    }

    //    @Async
    public void taskCreateIndexLemmas(PageDto pageDto, ConcurrentHashMap<String, Integer> lemmasMapOnSite) {
        if (pageDto.getContent().isBlank()) {
            return;
        }
        SiteEntity siteEntity = siteMapper.toSiteEntity(pageDto.getSiteDto());
        Map<String, Integer> lemmasRankOnPage = extractLemma.getMapLemmasOnPage(pageDto);
        for (String lemma : lemmasRankOnPage.keySet()) {
            int idLemma;
            if (lemmasMapOnSite.containsKey(lemma)) {
                idLemma = lemmasMapOnSite.get(lemma);
                lemmaIndexService.updateLemma(idLemma);
            } else {
                idLemma = lemmaIndexService.newLemmaSave(lemma, siteEntity);
                lemmasMapOnSite.put(lemma, idLemma);
            }
            lemmaIndexService.indexSave(idLemma, pageDto.getId(), (float) lemmasRankOnPage.get(lemma));
        }
    }

    public ConcurrentHashMap<String, Integer> getMapLemmas(SiteDto siteDto) {
        ConcurrentHashMap<String, Integer> lemmasMapSite = new ConcurrentHashMap<>();

        List<LemmaEntity> lemmaEntitiesList = lemmaIndexService.findAllLemmas(siteDto);
        lemmaEntitiesList.stream().map(lemmaEntity -> lemmasMapSite.put(lemmaEntity.getLemma(), lemmaEntity.getId()));
        return lemmasMapSite;
    }

}
