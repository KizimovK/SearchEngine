package searchengine.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.utils.extractPageOnSite.ParserSite;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;
import searchengine.services.SiteService;

@Component
@Slf4j
public class AsyncIndexing {
    private final SiteService siteService;
    private final ParserSite parserSite;
    private final CleanerOfData cleanerOfData;
    private final ConfigOptions sitesList;

    public AsyncIndexing(SiteService siteService, ParserSite parserSite,
                         CleanerOfData cleanerOfData, ConfigOptions siteList) {
        this.siteService = siteService;
        this.parserSite = parserSite;
        this.cleanerOfData = cleanerOfData;
        this.sitesList = siteList;
    }

    @Async
    public void taskIndexingSite(SiteDto siteDto) {
        cleanerOfData.removeSite(siteDto);
        siteDto = siteService.saveSite(siteDto);
        parserSite.startParserSite(siteDto);
        if (!siteService.getStatusIndexing(siteDto).equals(StatusIndexing.FAILED)) {
            siteService.updateStatusSite(siteDto, "", StatusIndexing.INDEXED);
            log.info("End parsing site " + siteDto.getUrl());
        }
    }
    @Async
    public void taskIndexingOnePage(String urlPage){
            String urlSite = siteService.getBaseUrlSite(urlPage);
            SiteConfig siteConfig =
                    sitesList.getSites().stream().filter(s -> s.getUrl().endsWith(urlSite)).findFirst().get();
            SiteDto siteDto = siteService.findSite(siteConfig.getUrl());
            if (siteDto == null) {
                siteDto = siteService.saveSite(siteService.getSiteFromConfig(siteConfig));
            } else {
                cleanerOfData.removeOnePage(urlPage, siteDto);
            }
            parserSite.startParserOnePage(urlPage, siteDto);
            log.info("End parsing one page " + urlPage);
    }
}
