package searchengine.companets;

import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.LemmaMapper;
import searchengine.repository.LemmaRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@Component
public class LemmaExtractorImpl implements LemmaExtractor {

    private static final String WORD_TYPE_REGEX = "\\W\\w&&[^а-яА-Я\\s]";
    private static final String[] particlesNames = new String[]{"МЕЖД", "ПРЕДЛ", "СОЮЗ"};
    private final LuceneMorphology luceneMorphology;
    private final LemmaRepository lemmaRepository;
    private final LemmaMapper lemmaMapper;

    public LemmaExtractorImpl(LuceneMorphology luceneMorphology, LemmaRepository lemmaRepository, LemmaMapper lemmaMapper) {
        this.luceneMorphology = luceneMorphology;
        this.lemmaRepository = lemmaRepository;
        this.lemmaMapper = lemmaMapper;
    }


    @Override
    public HashMap<String, Integer> getLemmas(String text) {
        HashMap<String, Integer> mapLemmas = new HashMap<>();
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
    public HashMap<String, Integer> getLemmasOnPage(PageDto pageDto) {
        String contentOutTag = clearContent(pageDto.getContent());
        return getLemmas(contentOutTag);
    }

    @Override
    public void saveLemmaOnPage(HashMap<String, Integer> mapLemmas, SiteDto siteDto) {
        lemmaRepository.flush();
        lemmaRepository.saveAll(lemmaMapper.toListLemmaEntity(mapLemmas, siteDto));
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
