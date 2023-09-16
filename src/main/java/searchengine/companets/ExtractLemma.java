package searchengine.companets;

import org.apache.lucene.morphology.LuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import searchengine.dto.data.PageDto;

import java.util.*;

@Component
public class ExtractLemma {
    private static final String WORD_TYPE_REGEX = "\\W\\w&&[^а-яА-Я\\s]";
    private static final String[] particlesNames = new String[]{"ПРЕДЛ",  "СОЮЗ", "МЕЖД", "ЧАСТ"};
    private final LuceneMorphology luceneMorphology;

    public ExtractLemma(LuceneMorphology luceneMorphology) {
        this.luceneMorphology = luceneMorphology;
    }

    public Map<String, Integer> getLemmas(String text) {
        TreeMap<String, Integer> mapLemmas = new TreeMap<>();
        List<String> lemmasList = getLemmasList(text);
        for (String lemma : lemmasList) {
            if (mapLemmas.containsKey(lemma)) {
                mapLemmas.put(lemma, mapLemmas.get(lemma) + 1);
            } else {
                mapLemmas.put(lemma, 1);
            }
        }
        return mapLemmas;
    }

    public List<String> getLemmasList(String text) {
        List<String> lemmaList = new ArrayList<>();
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
            lemmaList.add(normalWord);
        }
        return lemmaList;
    }

    public Map<String, Integer> getMapLemmasOnPage(PageDto pageDto) {
        String contentOutTag = clearContent(pageDto.getContent());
        return getLemmas(contentOutTag);
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

    public String[] arrayContainsRussianWords(String text) {
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
