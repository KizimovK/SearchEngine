package searchengine.companets;

import org.springframework.stereotype.Component;
import searchengine.companets.extractPageOnSite.ParserPage;
import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;
import searchengine.repository.PageRepository;

import java.util.List;
@Component
public class IndexingSiteImpl implements IndexingSite {
    private final SiteService siteService;
    private final PageService pageService;
    private final ParserPage parserPage;
    private final LemmaIndexExtractor lemmaIndexExtractor;

    public IndexingSiteImpl(SiteService siteService, PageService pageService, ParserPage parserPage,
                            LemmaIndexExtractor lemmaIndexExtractor) {
        this.siteService = siteService;
        this.pageService = pageService;
        this.parserPage = parserPage;
        this.lemmaIndexExtractor = lemmaIndexExtractor;
    }

    public void taskIndexingSite(SiteDto siteDto) {
        List<PageDto> pageDtoListOnSite = pageService.allSavePage(parserPage.startParserSite(siteDto));
        if (!siteService.getStatusIndexing(siteDto).equals(StatusIndexing.FAILED)) {
            siteService.updateStatusSite(siteDto, null, StatusIndexing.INDEXED);
        }
        List<LemmaDto> listAllLemmasOnSite = lemmaIndexExtractor.getAndSaveLemmasOnSite(siteDto, pageDtoListOnSite);
        lemmaIndexExtractor.getAndSaveIndexesOnSite(pageDtoListOnSite, listAllLemmasOnSite);
    }

}

