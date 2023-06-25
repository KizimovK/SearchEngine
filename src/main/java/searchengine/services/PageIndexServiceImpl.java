package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.companets.IndexingSite;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
@Slf4j
public class PageIndexServiceImpl implements PageIndexService {
    @Autowired
    private ConfigOptions sitesList;
    @Autowired
    private SiteService siteService;
    @Autowired
    private ConfigOptions configOptions;
    @Autowired
    private ParserPage parserPage;
    @Autowired
    private PageService pageService;
    ExecutorService executorService;

    @Override
    public void startIndexPage() throws ExecutionException, InterruptedException {
        List<SiteDto> siteListConfig = getSiteFromConfig();
        siteListConfig.forEach(siteDto -> {
            pageService.dropPagesSite(siteDto);
            siteService.dropSite(siteDto);
        });
        List<SiteDto> siteDtoList = new ArrayList<>();
        for (SiteDto siteDto : siteListConfig) {
            siteDtoList.add(siteService.saveSite(siteDto));
        }
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (SiteDto siteDto : siteDtoList) {
            executorService.submit(new IndexingSite(siteService, pageService, parserPage, siteDto));
        }
//        executorService.shutdown();
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
