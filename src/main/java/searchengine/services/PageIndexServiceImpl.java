package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import searchengine.companets.IndexingSite;
import searchengine.companets.LemmaIndexExtractor;
import searchengine.companets.PageService;
import searchengine.companets.SiteService;
import searchengine.companets.extractPageOnSite.ParserPage;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


@Service
@Slf4j
public class PageIndexServiceImpl implements PageIndexService {
    private final ConfigOptions sitesList;
    private final SiteService siteService;
    private final ConfigOptions configOptions;
    private final ParserPage parserPage;
    private final PageService pageService;
    private final  ExecutorService executorService;
    private final IndexingSite indexingSite;
    private final LemmaIndexExtractor lemmaIndexExtractor;

    public PageIndexServiceImpl(ConfigOptions sitesList, SiteService siteService,
                                ConfigOptions configOptions, ParserPage parserPage,
                                PageService pageService, ExecutorService executorService,
                                IndexingSite indexingSite, LemmaIndexExtractor lemmaIndexExtractor) {
        this.sitesList = sitesList;
        this.siteService = siteService;
        this.configOptions = configOptions;
        this.parserPage = parserPage;
        this.pageService = pageService;
        this.executorService = executorService;
        this.indexingSite = indexingSite;
        this.lemmaIndexExtractor = lemmaIndexExtractor;
    }

    @Override

    public void startIndexPage() {
        List<SiteDto> siteListConfig = getSiteFromConfig();
        siteListConfig.forEach(siteDto -> {
            pageService.dropPagesSite(siteDto);
            siteService.dropSite(siteDto);
        });
        List<SiteDto> siteDtoList = new ArrayList<>();
        for (SiteDto siteDto : siteListConfig) {
            siteDtoList.add(siteService.saveSite(siteDto));
        }

        for (SiteDto siteDto : siteDtoList) {
            Runnable task = ()-> indexingSite.taskIndexingSite(siteDto);
            executorService.submit(task);
        }
        executorService.shutdown();
    }
    @Override
    public boolean isActiveIndexing() {
        List<SiteDto> siteDtoList = siteService.findAllSite();
        for (SiteDto siteDto : siteDtoList){
            if (siteDto.getStatus().equals(StatusIndexing.INDEXING)){
                return  true;
            }
        }
        return false;
    }
    /*Todo: дописать stopIndexpage*/
    @Override
    public void stopIndexPage() {
        executorService.shutdownNow();
    }

    private List<SiteDto> getSiteFromConfig() {
        List<SiteDto> sitesDTOList = new ArrayList<>();
        int i = 1;
        for (SiteConfig siteConfig : sitesList.getSites()) {
            SiteDto siteDTO = new SiteDto();
            siteDTO.setId(i);
            siteDTO.setUrl(siteConfig.getUrl());
            siteDTO.setName(siteConfig.getName());
            siteDTO.setStatus(StatusIndexing.INDEXING);
            siteDTO.setStatusTime(LocalDateTime.now());
            siteDTO.setLastError(null);
            sitesDTOList.add(siteDTO);
            i++;
        }
        return sitesDTOList;
    }

}
