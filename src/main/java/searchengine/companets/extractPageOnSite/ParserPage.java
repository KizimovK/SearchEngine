package searchengine.companets.extractPageOnSite;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.companets.PageService;
import searchengine.companets.SiteService;
import searchengine.config.ConfigOptions;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Component

public class ParserPage {
    private final SiteService siteService;
    private final ConfigOptions configOptions;
    private final PageService pageService;

    @Autowired
    public ParserPage(SiteService serviceSite, ConfigOptions configOptions, PageService pageService) {
        this.siteService = serviceSite;
        this.configOptions = configOptions;
        this.pageService = pageService;
    }


    public List<PageDto> startParserSite(SiteDto siteDto) {
        String urlSite = siteDto.getUrl();
        String lastError;
        log.info("Parsing site ".concat(urlSite).concat(" ").concat(siteService.getSiteName(urlSite)));
        List<PageDto> pageDtoList = new CopyOnWriteArrayList<>();
        if (!Thread.interrupted()) {
            CopyOnWriteArrayList<PageDto> pageForkDtoList = new CopyOnWriteArrayList<>();
            CopyOnWriteArrayList<String> urlList = new CopyOnWriteArrayList<>();
            ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
            List<PageDto> pages = forkJoinPool.invoke(new ParserPageRecursiveTask(urlSite, siteDto, urlList,
                    pageForkDtoList, configOptions, pageService, siteService));
            pageDtoList.addAll(pages);
        } else {
            lastError = "Fork join exception.";
            log.error(lastError);
            siteService.updateStatusSite(siteDto, lastError, StatusIndexing.FAILED);
        }
//        siteService.updateStatusSite(siteDto, "", StatusIndexing.INDEXED);
        return pageDtoList;
    }
}