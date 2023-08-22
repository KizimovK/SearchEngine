package searchengine.companets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.companets.extractPageOnSite.ParserSite;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;
import searchengine.services.SiteService;

@Component
@Slf4j
public class AsyncIndexingSite {
    private final SiteService siteService;
    private final ParserSite parserSite;
    private final CleanerOfDataSite cleanerOfDataSite;

    public AsyncIndexingSite(SiteService siteService, ParserSite parserSite,
                             CleanerOfDataSite cleanerOfDataSite) {
        this.siteService = siteService;
        this.parserSite = parserSite;
        this.cleanerOfDataSite = cleanerOfDataSite;
    }

    @Async
    public void taskIndexingSite(SiteDto siteDto) {
        cleanerOfDataSite.cleaning(siteDto);
        siteDto = siteService.saveSite(siteDto);
        parserSite.startParserSite(siteDto);
        if (!siteService.getStatusIndexing(siteDto).equals(StatusIndexing.FAILED)) {
            siteService.updateStatusSite(siteDto, "", StatusIndexing.INDEXED);
            log.info("End parsing site " + siteDto.getUrl());
        }
    }
}
