package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.companets.AsyncIndexingSite;
import searchengine.companets.CleanerOfDataSite;
import searchengine.companets.extractPageOnSite.ParserPageTask;
import searchengine.companets.extractPageOnSite.ParserSite;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Service
@Slf4j
public class PageIndexServiceImpl implements PageIndexService {
    private final ConfigOptions sitesList;
    private final SiteService siteService;
    private final AsyncIndexingSite asyncIndexingSite;
    private final ParserSite parserSite;
    private final CleanerOfDataSite cleanerOfDataSite;
    private List<CompletableFuture<?>> futureList = new ArrayList<>();

    public PageIndexServiceImpl(ConfigOptions sitesList, SiteService siteService,
                                AsyncIndexingSite asyncIndexingSite, ParserSite parserSite,
                                CleanerOfDataSite cleanerOfDataSite) {

        this.sitesList = sitesList;
        this.siteService = siteService;
        this.asyncIndexingSite = asyncIndexingSite;
        this.parserSite = parserSite;
        this.cleanerOfDataSite = cleanerOfDataSite;
    }

    @Override
    public void startIndexedPagesAllSite() {
        List<SiteDto> siteListConfig = getListSitesFromConfig();
        ParserPageTask.begin();
        for (SiteDto siteDto : siteListConfig) {
            asyncIndexingSite.taskIndexingSite(siteDto);
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

    @Override
    public void stopIndexedPagesAllSite() {
        log.info("Stop indexing");
        ParserPageTask.stop();
    }

    @Override
    public boolean isPresentUrlPage(String urlPage) {
        var urlSite = getBaseUrlSite(urlPage);
        for (SiteConfig siteConfig : sitesList.getSites()) {
            if (siteConfig.getUrl().endsWith(urlSite)) {
                return true;
            }
        }
        return false;
    }

    /* Во первых расмотрим такое условие, если индексация, ни когда не запускалась,
    то данные об этом сайте надо отобразить в таблице sites,  и смело индексировать данную страницу.
    Если индексация данного сайта  уже производилась, то проверяем на наличее данной страницы в
    таблице pages.
    Если запись есть, то удаляем данные из таблиц pages и index_lemma. Затем только индексируем.
     */
    @Override
    public void indexOnePage(String urlPage) {
        String urlSite = getBaseUrlSite(urlPage);
        SiteConfig siteConfig =
                sitesList.getSites().stream().filter(s -> s.getUrl().endsWith(urlSite)).findFirst().get();

        SiteDto siteDto = siteService.findSite(siteConfig.getUrl());
        if (siteDto == null) {
            siteDto = siteService.saveSite(getSiteFromConfig(siteConfig));
        } else {
            cleanerOfDataSite.removeOnePage(urlPage, siteDto);
        }
        parserSite.startParserOnePage(urlPage, siteDto);
    }

    @Override
    public boolean isValidUrl(String urlPage) {
        if (urlPage.matches("^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            try {
                new URL(urlPage);
            } catch (MalformedURLException e) {
               return false;
            }
            return true;
        }
        return false;
    }

    private List<SiteDto> getListSitesFromConfig() {
        List<SiteDto> sitesDTOList = new ArrayList<>();
        int i = 1;
        for (SiteConfig siteConfig : sitesList.getSites()) {
            SiteDto siteDTO = getSiteFromConfig(siteConfig);
            siteDTO.setId(i);
            sitesDTOList.add(siteDTO);
            i++;
        }
        return sitesDTOList;
    }

    private SiteDto getSiteFromConfig(SiteConfig siteConfig) {
        SiteDto siteDTO = new SiteDto();
        siteDTO.setUrl(siteConfig.getUrl());
        siteDTO.setName(siteConfig.getName());
        siteDTO.setStatus(StatusIndexing.INDEXING);
        siteDTO.setStatusTime(LocalDateTime.now());
        siteDTO.setLastError(null);
        return siteDTO;
    }

    private String getBaseUrlSite(String urlPage) {
        if (urlPage.isEmpty()) {
            return "";
        }
        URL url = null;
        try {
            url = new URL(urlPage);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url.getHost();
    }

}
