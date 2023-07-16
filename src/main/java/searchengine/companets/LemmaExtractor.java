package searchengine.companets;

import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;

import java.util.HashMap;

public interface LemmaExtractor {
    HashMap<String, Integer> getLemmas(String text);

    HashMap<String, Integer> getLemmasOnPage(PageDto pageDto);


    void saveLemmaOnPage(HashMap<String, Integer> mapLemmas, SiteDto siteDto);
}
