package searchengine.services;

import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.util.List;

public interface SiteService {
    SiteDto saveSite(SiteDto siteDto);

    List<SiteDto> findAllSite();

    String getSiteName(String urlSite);

    void updateStatusSite(SiteDto siteDto, String lastError, StatusIndexing statusIndexing);

    void updateStatusTime(SiteDto siteDto);

    StatusIndexing getStatusIndexing(SiteDto siteDto);

    SiteDto findSite(String urlSite);

    String getBaseUrlSite(String urlPage);

    SiteDto getSiteFromConfig(SiteConfig siteConfig);
}
