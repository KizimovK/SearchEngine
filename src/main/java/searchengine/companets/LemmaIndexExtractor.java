package searchengine.companets;

import searchengine.dto.data.IndexDto;
import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;

import java.util.HashMap;
import java.util.List;

public interface LemmaIndexExtractor {
    HashMap<String, Integer> getLemmas(String text);

    HashMap<String, Integer> getMapLemmasOnPage(PageDto pageDto);
    List<LemmaDto> getLemmasOnSite(SiteDto siteDto, List<PageDto> pageDtoList);

    List<LemmaDto> saveLemmaOnSite(List<LemmaDto> listLemmasOnSite);
    List<LemmaDto> getAndSaveLemmasOnSite(SiteDto siteDto, List<PageDto> pageDtoList);
    List<IndexDto> getIndexesOnPage(List<LemmaDto> listLemmasOnSite, PageDto pageDto);
    List<IndexDto> getAllIndexesOnSite(List<PageDto> pageDtoList, List<LemmaDto> listLemmasOnSite);
    List<IndexDto> saveIndexOnSite(List<IndexDto> listIndexOnSite);
    List<IndexDto> getAndSaveIndexesOnSite(List<PageDto> pageDtoList, List<LemmaDto> listLemmasOnSite);
}
