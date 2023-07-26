package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.companets.*;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class PageIndexServiceImpl implements PageIndexService {
    private final ConfigOptions sitesList;
    private final SiteService siteService;
    private final ConfigOptions configOptions;
    private final PageService pageService;
    private final ExecutorService executorService;
    private final IndexingSite indexingSite;
    private final LemmaIndexExtractor lemmaIndexExtractor;
    private final CleanerOfDataSte cleanerSchemasSite;


    public PageIndexServiceImpl(ConfigOptions sitesList, SiteService siteService,
                                ConfigOptions configOptions,
                                PageService pageService, ExecutorService executorService,
                                IndexingSite indexingSite, LemmaIndexExtractor lemmaIndexExtractor, CleanerOfDataSte cleanerSchemasSite) {
        this.sitesList = sitesList;
        this.siteService = siteService;
        this.configOptions = configOptions;
        this.pageService = pageService;
        this.executorService = executorService;
        this.indexingSite = indexingSite;
        this.lemmaIndexExtractor = lemmaIndexExtractor;
        this.cleanerSchemasSite = cleanerSchemasSite;
    }

    @Override

    public void startIndexPage() {
        List<SiteDto> siteListConfig = getSiteFromConfig();
        for (SiteDto siteDto : siteListConfig) {
            Runnable task = () -> indexingSite.taskIndexingSite(siteDto);
            executorService.submit(task);
        }


    }

    @Override
    public boolean isActiveIndexing() {
        List<SiteDto> siteDtoList = siteService.findAllSite();
        for (SiteDto siteDto : siteDtoList) {
            if (siteDto.getStatus().equals(StatusIndexing.INDEXING)) {
                return true;
            }
        }
        return false;
    }

    /*Todo: дописать stopIndexpage*/
    @Override
    public void stopIndexPage() {
        log.info("Stop indexing");
        executorService.shutdownNow(); // initiate shutdown
        List<SiteDto> siteDtoList = siteService.findAllSite();
        siteDtoList.forEach(siteDto ->
                siteService.updateStatusSite(siteDto, "Stop Indexing", StatusIndexing.FAILED));
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
