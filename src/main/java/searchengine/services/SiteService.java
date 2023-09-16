package searchengine.services;

import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.util.List;

public interface SiteService {

    void allSaveSites(List<SiteDto> siteDtoList);

    SiteDto saveSite(SiteDto siteDto);

    List<SiteDto> findAllSite();

    String getSiteName(String urlSite);

    void updateStatusSite(SiteDto siteDto, String lastError, StatusIndexing statusIndexing);

    void updateStatusTime(SiteDto siteDto);
    StatusIndexing getStatusIndexing(SiteDto siteDto);
    SiteDto findSite(String urlSite);
}
