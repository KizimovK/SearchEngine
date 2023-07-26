package searchengine.companets;

import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import searchengine.dto.data.IndexDto;
import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.IndexMapper;
import searchengine.mapping.LemmaMapper;
import searchengine.model.IndexEntity;
import searchengine.model.LemmaEntity;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;

import java.util.*;

@Component
public class LemmaIndexExtractorImpl implements LemmaIndexExtractor {

    private static final String WORD_TYPE_REGEX = "\\W\\w&&[^а-яА-Я\\s]";
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};
    private final LuceneMorphology luceneMorphology;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final LemmaMapper lemmaMapper;
    private final IndexMapper indexMapper;

    public LemmaIndexExtractorImpl(LuceneMorphology luceneMorphology, LemmaRepository lemmaRepository,
                                   IndexRepository indexRepository, LemmaMapper lemmaMapper, IndexMapper indexMapper) {
        this.luceneMorphology = luceneMorphology;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.lemmaMapper = lemmaMapper;
        this.indexMapper = indexMapper;
    }


    @Override
    public Map<String, Integer> getLemmas(String text) {
        TreeMap<String, Integer> mapLemmas = new TreeMap<>();
        String[] words = arrayContainsRussianWords(text);
        for (String word : words) {
            if (word.isBlank() || !isCorrectWordForm(word)) {
                continue;
            }
            List<String> wordBaseForms = luceneMorphology.getMorphInfo(word);
            if (anyWordBaseBelongToParticle(wordBaseForms)) {
                continue;
            }
            List<String> normalForms = luceneMorphology.getNormalForms(word);
            if (normalForms.isEmpty()) {
                continue;
            }
            String normalWord = normalForms.get(0);
            if (mapLemmas.containsKey(normalWord)) {
                mapLemmas.put(normalWord, mapLemmas.get(normalWord) + 1);
            } else {
                mapLemmas.put(normalWord, 1);
            }
        }
        return mapLemmas;
    }

    @Override
    public Map<String, Integer> getMapLemmasOnPage(PageDto pageDto) {
        String contentOutTag = clearContent(pageDto.getContent());
        return getLemmas(contentOutTag);
    }


    @Override
    public List<LemmaDto> getLemmasOnSite(SiteDto siteDto, List<PageDto> pageDtoList) {
        HashMap<String, Integer> allLemmasOnSite = new HashMap<>();
        pageDtoList.stream().filter(pageDto -> pageDto.getContent() != null).forEach(pageDto -> {
            Map<String, Integer> mapLemmas = this.getMapLemmasOnPage(pageDto);
            mapLemmas.keySet().forEach(lemma -> {
                if (allLemmasOnSite.containsKey(lemma)) {
                    allLemmasOnSite.put(lemma, allLemmasOnSite.get(lemma) + 1);
                } else {
                    allLemmasOnSite.put(lemma, 1);
                }
            });
        });
        return lemmaMapper.toListLemmaDto(allLemmasOnSite, siteDto);
    }

    public List<IndexDto> getIndexesOnPage(List<LemmaDto> listLemmasOnSite, PageDto pageDto) {
        Map<String, Integer> mapLemmasOnPage = this.getMapLemmasOnPage(pageDto);
        List<IndexDto> listIndexOnPage = new ArrayList<>(mapLemmasOnPage.size());
        listLemmasOnSite.forEach(lemmaDto -> {
            IndexDto indexDto = new IndexDto();
            if (mapLemmasOnPage.containsKey(lemmaDto.getLemma())) {
                indexDto.setLemmaDto(lemmaDto);
                indexDto.setPageDto(pageDto);
                float rank = mapLemmasOnPage.get(lemmaDto.getLemma());
                indexDto.setRank(rank);
                listIndexOnPage.add(indexDto);
            }
        });
        return listIndexOnPage;
    }

    @Override
    public List<IndexDto> getAllIndexesOnSite(List<PageDto> pageDtoList, List<LemmaDto> listLemmasOnSite) {
        List<IndexDto> listAllIndexOnSite = new ArrayList<>();
        pageDtoList.stream().filter(pageDto -> pageDto.getContent() != null).forEach(pageDto ->
                listAllIndexOnSite.addAll(this.getIndexesOnPage(listLemmasOnSite, pageDto)));
        return listAllIndexOnSite;
    }

    @Override
    public List<IndexDto> saveIndexOnSite(List<IndexDto> listIndexOnSite) {
        indexRepository.flush();
        List<IndexEntity> indexEntityList = indexRepository.saveAll(indexMapper.toListIndexEntity(listIndexOnSite));
        return indexMapper.toListIndexDto(indexEntityList);
    }

    @Override
    public List<IndexDto> getAndSaveIndexesOnSite(List<PageDto> pageDtoList, List<LemmaDto> listLemmasOnSite) {
        return this.saveIndexOnSite(this.getAllIndexesOnSite(pageDtoList, listLemmasOnSite));
    }


    @Override
    public List<LemmaDto> saveLemmaOnSite(List<LemmaDto> listLemmasOnSite) {
        lemmaRepository.flush();
        List<LemmaEntity> lemmaEntityList = lemmaRepository.saveAll(lemmaMapper.toListLemmaEntity(listLemmasOnSite));
        return lemmaMapper.toListLemmaDto(lemmaEntityList);
    }

    @Override
    public List<LemmaDto> getAndSaveLemmasOnSite(SiteDto siteDto, List<PageDto> pageDtoList) {
        return this.saveLemmaOnSite(this.getLemmasOnSite(siteDto, pageDtoList));
    }


    private String clearContent(String content) {
        return Jsoup.parse(content).text();
    }

    private boolean anyWordBaseBelongToParticle(List<String> wordBaseForms) {
        return wordBaseForms.stream().anyMatch(this::hasParticleProperty);
    }

    private boolean hasParticleProperty(String wordBase) {
        for (String property : particlesNames) {
            if (wordBase.toUpperCase().contains(property)) {
                return true;
            }
        }
        return false;
    }

    private String[] arrayContainsRussianWords(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("([^а-я\\s])", " ")
                .trim()
                .split("\\s+");
    }

    private boolean isCorrectWordForm(String word) {
        List<String> wordInfo = luceneMorphology.getMorphInfo(word);
        for (String morphInfo : wordInfo) {
            if (morphInfo.matches(WORD_TYPE_REGEX)) {
                return false;
            }
        }
        return true;
    }


}
