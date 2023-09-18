package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.utils.AsyncIndexing;
import searchengine.utils.extractPageOnSite.ParserPageTask;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
public class PageIndexServiceImpl implements PageIndexService {
    private final ConfigOptions sitesList;
    private final SiteService siteService;
    private final AsyncIndexing asyncIndexing;

    public PageIndexServiceImpl(ConfigOptions sitesList, SiteService siteService,
                                AsyncIndexing asyncIndexing) {

        this.sitesList = sitesList;
        this.siteService = siteService;
        this.asyncIndexing = asyncIndexing;
    }

    @Override
    public void startIndexedPagesAllSite() {
        List<SiteDto> siteListConfig = getListSitesFromConfig();
        ParserPageTask.begin();
        for (SiteDto siteDto : siteListConfig) {
            asyncIndexing.taskIndexingSite(siteDto);
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
        var urlSite = siteService.getBaseUrlSite(urlPage);
        for (SiteConfig siteConfig : sitesList.getSites()) {
            if (siteConfig.getUrl().endsWith(urlSite)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void indexOnePage(String urlPage) {
        asyncIndexing.taskIndexingOnePage(urlPage);
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
            SiteDto siteDTO = siteService.getSiteFromConfig(siteConfig);
            siteDTO.setId(i);
            sitesDTOList.add(siteDTO);
            i++;
        }
        return sitesDTOList;
    }

}
