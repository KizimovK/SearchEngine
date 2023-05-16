package searchengine.companets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.SiteMapper;
import searchengine.model.Site;
import searchengine.model.StatusIndexing;
import searchengine.repository.SiteRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
public class ServiceSite {
    private SiteRepository siteRepository;
    private SiteMapper siteMapper;
    private final ConfigOptions configOptions;

    @Autowired
    public ServiceSite(SiteRepository siteRepository, SiteMapper siteMapper, ConfigOptions configOptions) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.configOptions = configOptions;
    }

    public void allSaveSites(List<SiteDto> siteDtoList){
        siteRepository.saveAll(siteMapper.mappingToSiteList(siteDtoList));
    }
    public void updateSite(SiteDto siteDto){
        siteRepository.save(siteMapper.mappingToSite(siteDto));
    }

    public List<SiteDto> findAllSite(){
        List<SiteDto> siteDtoList = new ArrayList<>();
        siteDtoList.addAll(siteMapper.mappingToSiteDtoList((List<Site>) siteRepository.findAll()));
        return  siteDtoList;
    }
    public void clearSite(String urlSite){
        if (siteRepository.findByUrl(urlSite) != null) {
            log.info("start site date delete from ".concat(urlSite));
            Site site = siteRepository.findByUrl(urlSite);
            site.setStatus(StatusIndexing.INDEXING);
            site.setName(getSiteName(urlSite));
            site.setStatusTime(LocalDateTime.now());
            siteRepository.saveAndFlush(site);
            siteRepository.delete(site);
        }
    }
    public String getSiteName(String urlSite) {
        return configOptions.getSites().stream()
                .filter(site -> site.getUrl().equals(urlSite))
                .findFirst()
                .map(SiteConfig::getName)
                .orElse("");
    }


}
