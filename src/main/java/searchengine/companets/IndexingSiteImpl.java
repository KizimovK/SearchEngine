package searchengine.companets;

import org.springframework.stereotype.Component;
import searchengine.companets.extractPageOnSite.ParserPage;
import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.util.List;
@Component
public class IndexingSiteImpl implements IndexingSite {
    private final SiteService siteService;
    private final PageService pageService;
    private final ParserPage parserPage;
    private final LemmaIndexExtractor lemmaIndexExtractor;
    private final CleanerOfDataSte cleanerSchemasSite;

    public IndexingSiteImpl(SiteService siteService, PageService pageService, ParserPage parserPage,
                            LemmaIndexExtractor lemmaIndexExtractor, CleanerOfDataSte cleanerSchemasSite) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.parserPage = parserPage;
        this.lemmaIndexExtractor = lemmaIndexExtractor;
        this.cleanerSchemasSite = cleanerSchemasSite;
    }

    public void taskIndexingSite(SiteDto siteDto) {
        cleanerSchemasSite.cleaning(siteDto);
        siteDto = siteService.saveSite(siteDto);
        List<PageDto> pageDtoListOnSite = pageService.allSavePage(parserPage.startParserSite(siteDto));
        if (!siteService.getStatusIndexing(siteDto).equals(StatusIndexing.FAILED)) {
            siteService.updateStatusSite(siteDto, null, StatusIndexing.INDEXED);
        }
        List<LemmaDto> listAllLemmasOnSite = lemmaIndexExtractor.getAndSaveLemmasOnSite(siteDto, pageDtoListOnSite);
        lemmaIndexExtractor.getAndSaveIndexesOnSite(pageDtoListOnSite, listAllLemmasOnSite);
    }

}

