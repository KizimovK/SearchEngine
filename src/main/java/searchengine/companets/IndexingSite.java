package searchengine.companets;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.companets.extractPageOnSite.ParserPage;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class IndexingSite implements Runnable {

    private SiteService siteService;
    private PageService pageService;
    private ParserPage parserPage;
    private LemmaExtractor lemmaExtractor;
    private SiteDto siteDto;


    public IndexingSite(SiteService siteService, PageService pageService,
                        ParserPage parserPage, LemmaExtractor lemmaExtractor, SiteDto siteDto) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.parserPage = parserPage;
        this.lemmaExtractor = lemmaExtractor;
        this.siteDto = siteDto;
    }

    @Override
    public void run() {
        List<PageDto> pageDtoList = parserPage.startParserSite(siteDto);
        pageService.allSavePage(pageDtoList);
        if (!siteService.getStatusIndexing(siteDto).equals(StatusIndexing.FAILED)) {
            siteService.updateStatusSite(siteDto, null, StatusIndexing.INDEXED);
        }
        pageDtoList.forEach(pageDto -> {
            HashMap<String, Integer> mapLemmas = lemmaExtractor.getLemmasOnPage(pageDto);
            lemmaExtractor.saveLemmaOnPage(mapLemmas,siteDto);
        });
    }
}
