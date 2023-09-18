package searchengine.utils.extractPageOnSite;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.utils.MakerIndex;
import searchengine.config.ConfigOptions;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;
import searchengine.services.PageService;
import searchengine.services.SiteService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Component

public class ParserSite {
    private final SiteService siteService;
    private final ConfigOptions configOptions;
    private final PageService pageService;

    private final MakerIndex makerIndex;


    public ParserSite(SiteService serviceSite, ConfigOptions configOptions,
                      PageService pageService, MakerIndex makerIndex) {
        this.siteService = serviceSite;
        this.configOptions = configOptions;
        this.pageService = pageService;
        this.makerIndex = makerIndex;
    }


    public void startParserSite(SiteDto siteDto) {
        String urlSite = siteDto.getUrl().concat("/");
        String lastError;
        log.info("Parsing site ".concat(urlSite).concat(" ").concat(siteService.getSiteName(urlSite)));
        if (!Thread.interrupted()) {
            ConcurrentHashMap<String, Integer> lemmasMapOnSite = new ConcurrentHashMap<>();
            CopyOnWriteArraySet<String> urlSet = new CopyOnWriteArraySet<>();
            ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            forkJoinPool.invoke(new ParserPageTask(urlSite, siteDto,
                    urlSet, configOptions, pageService, siteService, makerIndex, lemmasMapOnSite));
        } else {
            lastError = "Fork join exception.";
            log.error(lastError);
            siteService.updateStatusSite(siteDto, lastError, StatusIndexing.FAILED);
        }
    }

    public void startParserOnePage(String urlPage, SiteDto siteDto) {
        ParserPageTask parserPageTask = new ParserPageTask(configOptions, pageService,
                siteService, makerIndex);
        log.info("Parsing one webpage: " + urlPage);
        parserPageTask.parserPageOneTask(urlPage, siteDto);
    }
}