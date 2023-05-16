package searchengine.companets.extractPageOnSite;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.companets.PageService;
import searchengine.companets.ServiceSite;
import searchengine.config.ConfigOptions;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Component
public class ParserPage {
//    private SiteDto siteDto;
    private final ServiceSite serviceSite;

    private final ConfigOptions configOptions;
    private final PageService pageService;
@Autowired
    public ParserPage(ServiceSite serviceSite, ConfigOptions configOptions, PageService pageService) {
        this.serviceSite = serviceSite;
    this.configOptions = configOptions;
    this.pageService = pageService;
}



    public boolean startParserSite(SiteDto siteDto){
        String urlSite = siteDto.getUrl();
        log.info("Site indexing start ".concat(urlSite).concat(" ").concat(serviceSite.getSiteName(urlSite)) );

            if (!Thread.interrupted()) {
                List<PageDto> pageDtoList = new CopyOnWriteArrayList<>();
                if (!Thread.interrupted()) {
                    //String url = urlSite.concat("/");

                    CopyOnWriteArrayList<PageDto> pageForkDtoList = new CopyOnWriteArrayList<>();

                    CopyOnWriteArrayList<String> urlList = new CopyOnWriteArrayList<>();

                    ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

                    List<PageDto> pages =
                            forkJoinPool.invoke(new ParserPageRecursiveTask(urlSite,siteDto,urlList,
                                    pageForkDtoList,configOptions, pageService));
                    pageDtoList.addAll(pages);
                } else log.error("Fork join exception.");
                for (PageDto pageDto : pageDtoList){
                    System.out.println(pageDto.getPath());
                }

                pageService.allSavePage(pageDtoList);
            }
            else {
                log.error("Local interrupted exception.");
            }
        System.out.println();
        return true;
}
}
