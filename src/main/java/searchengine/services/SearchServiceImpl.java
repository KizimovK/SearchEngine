package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.companets.ExtractLemma;
import searchengine.dto.searchs.SearchDto;
import searchengine.dto.data.SiteDto;
import searchengine.dto.searchs.SearchResponse;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;

import java.util.*;
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
    private ExtractLemma extractLemma;
    private SiteService siteService;
    private LemmaRepository lemmaRepository;
    private PageRepository pageRepository;
    private IndexRepository indexRepository;

    public SearchServiceImpl(ExtractLemma extractLemma, SiteService siteService, LemmaRepository lemmaRepository, PageRepository pageRepository, IndexRepository indexRepository) {
        this.extractLemma = extractLemma;
        this.siteService = siteService;
        this.lemmaRepository = lemmaRepository;
        this.pageRepository = pageRepository;
        this.indexRepository = indexRepository;
    }

    @Override
    public SearchResponse getResponseSearchQuery(String query, String site, int offset, int limit) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setResult(true);
        searchResponse.setCount(100);
        List<String> lemmasQueryList = extractLemma.getLemmas(query).keySet().stream().toList();
        getResultSearchFromOneSite(lemmasQueryList,site);

        return searchResponse;
    }

    private List<SearchDto> getResultSearchFromOneSite(List<String> lemmasQueryList, String site) {
        SiteDto siteDto = siteService.findSite(site);
        Map<PageEntity, Float> pageRelevanceMap = new HashMap<>();
        List<LemmaEntity> lemmasList = getLemmasFromSite(lemmasQueryList, siteDto);
        List<PageEntity> pagesList = pageRepository.findPagesByLemmas(lemmasList);
        for (PageEntity pageEntity : pagesList){
            List<IndexEntity> indexEntityList = indexRepository.findIndexByPageAndLemmasList(pageEntity,lemmasList);
            if (indexEntityList!=null){
                indexEntityList.forEach(indexEntity -> pageRelevanceMap.merge(pageEntity,
                        indexEntity.getRank(), (k, v) -> v + indexEntity.getRank()));
            }
        }
        log.info("End search!");
        return null;
    }

    private List<LemmaEntity> getLemmasFromSite(List<String> lemmasQueryList, SiteDto siteDto) {
        lemmaRepository.flush();
        List<LemmaEntity> lemmasList = lemmaRepository.findLemmasListBySite(lemmasQueryList, siteDto.getId());
        lemmasList.sort(Comparator.comparingInt(LemmaEntity::getFrequency));
        return lemmasList;
    }


}
