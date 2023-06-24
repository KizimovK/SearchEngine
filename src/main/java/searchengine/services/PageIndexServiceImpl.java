package searchengine.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.companets.IndexingSite;
import searchengine.companets.PageService;
import searchengine.companets.SiteService;
import searchengine.companets.extractPageOnSite.ParserPage;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.dto.response.Response;
import searchengine.model.StatusIndexing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
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
    private List<Future> futureList = new ArrayList<>();
    private boolean isActiveIndexing = false;

    @Override
    public Response startIndexPage() throws ExecutionException, InterruptedException {
        ExecutorService executorService;
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
            futureList.add(executorService.submit(new IndexingSite(siteService, pageService, parserPage, siteDto)));
        }
        for (Future future : futureList) {
            future.get();
        }
        isActiveIndexing = true;
        return new Response(true, "");
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
