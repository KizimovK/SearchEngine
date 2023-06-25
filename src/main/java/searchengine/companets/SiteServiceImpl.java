package searchengine.companets;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.SiteMapper;
import searchengine.model.Site;
import searchengine.model.StatusIndexing;
import searchengine.repository.SiteRepository;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class SiteServiceImpl implements SiteService {
    private final ConfigOptions configOptions;
    private final EntityManagerFactory entityManagerFactory;
    private SiteRepository siteRepository;
    private SiteMapper siteMapper;

    @Autowired
    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper, ConfigOptions configOptions, SessionFactory sessionFactory, EntityManagerFactory entityManagerFactory) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.configOptions = configOptions;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void allSaveSites(List<SiteDto> siteDtoList) {
        siteRepository.saveAll(siteMapper.mappingToSiteList(siteDtoList));
    }

    @Override
    public SiteDto saveSite(SiteDto siteDto) {
        return siteMapper.mappingToSiteDto(siteRepository.saveAndFlush(siteMapper.mappingToSite(siteDto)));
    }

    @Override
    public List<SiteDto> findAllSite() {
        siteRepository.flush();
        List<SiteDto> siteDtoList = new ArrayList<>();
        siteDtoList.addAll(siteMapper.mappingToSiteDtoList((List<Site>) siteRepository.findAll()));
        return siteDtoList;
    }

    @Override
    public void dropSite(SiteDto siteDto) {
        String urlSite = siteDto.getUrl();
        List<Site> siteList = siteRepository.findAllByUrl(urlSite);
        if (!siteList.isEmpty()) {
            log.info("start site date delete from ".concat(urlSite));
            siteRepository.deleteAll(siteList);
        }
    }

    @Override
    public String getSiteName(String urlSite) {
        return configOptions.getSites().stream()
                .filter(site -> site.getUrl().equals(urlSite))
                .findFirst()
                .map(SiteConfig::getName)
                .orElse("");
    }

    @Override
    public int getSiteId(String urlSite) {
        return siteRepository.findByUrl(urlSite).getId();
    }

    @Override
    public void updateStatusSite(SiteDto siteDto, String lastError, StatusIndexing statusIndexing) {
        siteDto.setStatus(statusIndexing);
        siteDto.setLastError(lastError);
        siteDto.setStatusTime(LocalDateTime.now());
        saveSite(siteDto);
    }

    @Override
    public void updateStatusTime(SiteDto siteDto) {
        siteDto.setStatusTime(LocalDateTime.now());
        Session session = getSession();
        Transaction tx1 = session.beginTransaction();
        session.update(siteMapper.mappingToSite(siteDto));
        tx1.commit();
        session.close();
    }

    @Override
    public StatusIndexing getStatusIndexing(SiteDto siteDto) {
        return siteRepository.findById(siteDto.getId()).get().getStatus();
    }

    private Session getSession() {
        Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
        return session;
    }

}
