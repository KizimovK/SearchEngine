package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import searchengine.companets.ExtractLemma;
import searchengine.dto.data.SiteDto;
import searchengine.dto.searchs.SearchDto;
import searchengine.dto.searchs.SearchResponse;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
    private ExtractLemma extractLemma;
    private SiteService siteService;
    private LemmaRepository lemmaRepository;
    private PageRepository pageRepository;
    private IndexRepository indexRepository;
    private Map<PageEntity, Float> pageRelevanceMap = new LinkedHashMap<>();
    private String query = null;
    private String site = null;
    private List<String> lemmasQueryList = new ArrayList<>();


    public SearchServiceImpl(ExtractLemma extractLemma, SiteService siteService, LemmaRepository lemmaRepository, PageRepository pageRepository, IndexRepository indexRepository) {
        this.extractLemma = extractLemma;
        this.siteService = siteService;
        this.lemmaRepository = lemmaRepository;
        this.pageRepository = pageRepository;
        this.indexRepository = indexRepository;
    }

    @Override
    public SearchResponse getResponseSearch(String query, String site, int offset, int limit) {
        List<SiteDto> siteList = new ArrayList<>();
        if (this.site == null || !this.site.equals(site)) {
            if (site.isEmpty()) {
                siteList = siteService.findAllSite();
            } else {
                siteList.add(siteService.findSite(site));
            }
        }
        SearchResponse searchResponse = new SearchResponse();
        if (this.query == null || !this.query.equals(query) || !this.site.equals(site)) {
            this.site = site;
            this.query = query;
            lemmasQueryList = extractLemma.getLemmasList(query);
            pageRelevanceMap = getSearchPage(lemmasQueryList, siteList);
            if (pageRelevanceMap.isEmpty()) {
                searchResponse.setResult(false);
                return searchResponse;
            }
        }
        searchResponse.setResult(true);
        searchResponse.setCount(pageRelevanceMap.size());
        searchResponse.setData(getSearchDataResponseList(pageRelevanceMap, lemmasQueryList, offset, limit));
        log.info("Search end........");
        return searchResponse;
    }

    private List<SearchDto> getSearchDataResponseList(Map<PageEntity, Float> pageRelevanceMap,
                                                      List<String> lemmasQueryList, int offset, int limit) {
        List<SearchDto> searchDataList = new ArrayList<>();
        pageRelevanceMap = pageRelevanceMap.entrySet().stream().skip(offset).limit(limit).
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (f1, f2) -> f1, LinkedHashMap::new));
        for (PageEntity page : pageRelevanceMap.keySet()) {
            SearchDto searchDto = new SearchDto();
            searchDto.setSite(page.getSiteEntity().getUrl());
            searchDto.setSiteName(page.getSiteEntity().getName());
            searchDto.setUri(page.getPath());
            searchDto.setTitle(getTitlePage(page));
            searchDto.setSnippet(getSnippetText(page.getContent(), lemmasQueryList, page.getPath()));
            searchDto.setRelevance(pageRelevanceMap.get(page));
            searchDataList.add(searchDto);
        }
        return searchDataList;
    }


    private String getTitlePage(PageEntity page) {
        return Jsoup.parse(page.getContent()).title();
    }


    private Map<PageEntity, Float> getSearchPage(List<String> lemmasQueryList, List<SiteDto> siteList) {
        List<LemmaEntity> lemmasList = new ArrayList<>();
        siteList.forEach(siteDto -> lemmasList.addAll(getLemmasFromSite(lemmasQueryList, siteDto)));
        Map<PageEntity, Float> pageRelevanceAbsMap = new HashMap<>();
        if (lemmasList.isEmpty()) {
            return pageRelevanceAbsMap;
        }
        List<PageEntity> pagesList = pageRepository.findPagesByLemmas(lemmasList);
        Map<PageEntity, Integer> countLemmasInPageMap = new HashMap<>();
        for (PageEntity pageEntity : pagesList) {
            List<IndexEntity> indexEntityList = indexRepository.findIndexByPageAndLemmasList(pageEntity, lemmasList);
            if (indexEntityList != null) {
                indexEntityList.forEach(indexEntity -> pageRelevanceAbsMap.merge(pageEntity,
                        indexEntity.getRank(), (k, v) -> v + indexEntity.getRank()));
                countLemmasInPageMap.put(pageEntity, indexEntityList.size());
            }
        }
        float maxRelevancePage = Collections.max(pageRelevanceAbsMap.values());
        Map<PageEntity, Float> pageRelevanceMap = new LinkedHashMap<>();
        while (!countLemmasInPageMap.isEmpty()) {
            int maxCountLemma = Collections.max(countLemmasInPageMap.values());
            List<PageEntity> pageListInMaxCount = countLemmasInPageMap.keySet().stream().
                    filter(p -> countLemmasInPageMap.get(p) == maxCountLemma).collect(Collectors.toList());
            Map<PageEntity, Float> pageRelevanceTempMap = pageListInMaxCount.stream().map(page ->
                            new AbstractMap.SimpleEntry<>(page, pageRelevanceAbsMap.get(page) / maxRelevancePage)).
                    sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue())).
                    collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (f1, f2) -> f1, LinkedHashMap::new));
            pageRelevanceMap.putAll(pageRelevanceTempMap);
            countLemmasInPageMap.keySet().removeAll(pageListInMaxCount);
        }
        return pageRelevanceMap;
    }

    private String getSnippetText(String content, List<String> lemmasQueryList, String path) {
        Document document = Jsoup.parse(content);
        String bodyContent = document.body().text();
        Map<String, Integer> positionsMap = getPositionsMap(bodyContent, lemmasQueryList);
        List<String> wordBoltList = new ArrayList<>();
        String resultSnippet = "";
        for (int position : positionsMap.values()) {
            int beginPosition = bodyContent.indexOf(" ", position - 75 < 0 ? 0 : position - 75);
            int endPosition = bodyContent.lastIndexOf(" ", position + 75 > bodyContent.length() ?
                    bodyContent.length() : position + 75);
            wordBoltList.add(bodyContent.substring(position, bodyContent.indexOf(" ", position)));
            String currentResult = bodyContent.substring(beginPosition, endPosition);
            resultSnippet = joinTwoString(resultSnippet, currentResult);
        }
        return setBoltWords(resultSnippet, wordBoltList);
    }

    private String setBoltWords(String text, List<String> wordBoltList) {
        for (String wordBolt : wordBoltList) {
            text = text.replaceAll(wordBolt, "<b>" + wordBolt + "</b>");
        }
        return text;
    }

    private String joinTwoString(String one, String two) {
        Set<String> resultSet = new LinkedHashSet<>(List.of(one.split("\\s+")));
        resultSet.addAll(List.of(two.split("\\s+")));
        StringBuilder result = new StringBuilder();
        for (String s : resultSet) {
            result.append(s).append(" ");
        }
        return result.toString();
    }

    //Получение позиций ближайших лемм в контенте
    private Map<String, Integer> getPositionsMap(String bodyContent, List<String> lemmasQueryList) {
        Map<Integer, String> allLemmasAllPositionsMap = new TreeMap<>();
        for (String lemma : lemmasQueryList) {
            List<Integer> indexLemmaInContent = getIndexLemmaInContent(bodyContent, lemma);
            indexLemmaInContent.forEach(i -> allLemmasAllPositionsMap.put(i, lemma));
        }
        Map<String, Integer> positionsMap = new LinkedHashMap<>();
        String lastLemma = null;
        for (int position : allLemmasAllPositionsMap.keySet()) {
            String lemma = allLemmasAllPositionsMap.get(position);
            if (positionsMap.isEmpty() || !positionsMap.containsKey(lemma)) {
                positionsMap.put(lemma, position);
                lastLemma = lemma;
            }
            int lastPosition = positionsMap.get(lastLemma);
            if (!lastLemma.equals(lemma) && (position > lastPosition - 100) && (position < lastPosition + 100)) {
                positionsMap.put(lemma, position);
                lastLemma = lemma;
            }
        }
        return positionsMap;
    }

    //   Получение всех позиций, одной леммы в контенте
    private List<Integer> getIndexLemmaInContent(String bodyContent, String lemma) {
        TreeSet<Integer> indexLemmaInText = new TreeSet<>();
        bodyContent = bodyContent.toLowerCase(Locale.ROOT);
        String[] words = extractLemma.arrayContainsRussianWords(bodyContent);
        int index = 0;
        for (String word : words) {
            List<String> wordLemmas = extractLemma.getLemmasList(word);
            if (!wordLemmas.isEmpty() && wordLemmas.get(0).equals(lemma)) {
                index = bodyContent.indexOf(word, index);
                if (index > 0) {
                    indexLemmaInText.add(index);
                }
            }
        }
        return new ArrayList<>(indexLemmaInText);
    }

    private List<LemmaEntity> getLemmasFromSite(List<String> lemmasQueryList, SiteDto siteDto) {
        lemmaRepository.flush();
        List<LemmaEntity> lemmasList = lemmaRepository.findLemmasListBySite(lemmasQueryList, siteDto.getId());
        lemmasList.sort(Comparator.comparingInt(LemmaEntity::getFrequency));
        return lemmasList;
    }


}
