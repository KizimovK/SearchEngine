package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.SiteMapper;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndexing;
import searchengine.repository.SiteRepository;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service

public class SiteServiceImpl implements SiteService {
    private final ConfigOptions configOptions;
    private final EntityManagerFactory entityManagerFactory;
    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;


    public SiteServiceImpl(SiteRepository siteRepository, SiteMapper siteMapper,
                           ConfigOptions configOptions, EntityManagerFactory entityManagerFactory) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.configOptions = configOptions;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void allSaveSites(List<SiteDto> siteDtoList) {
        siteRepository.flush();
        siteRepository.saveAll(siteMapper.toSiteEntityList(siteDtoList));
    }

    @Override
    public SiteDto saveSite(SiteDto siteDto) {
        return siteMapper.toSiteDto(siteRepository.saveAndFlush(siteMapper.toSiteEntity(siteDto)));
    }

    @Override
    public List<SiteDto> findAllSite() {
        siteRepository.flush();
        List<SiteDto> siteDtoList = new ArrayList<>();
        siteDtoList.addAll(siteMapper.toSiteDtoList((List<SiteEntity>) siteRepository.findAll()));
        return siteDtoList;
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
    public void updateStatusSite(SiteDto siteDto, String lastError, StatusIndexing statusIndexing) {
        siteRepository.flush();
        siteDto.setStatus(statusIndexing);
        siteDto.setLastError(lastError);
        siteDto.setStatusTime(LocalDateTime.now());
        saveSite(siteDto);
    }

    @Override
    public void updateStatusTime(SiteDto siteDto) {
        siteRepository.flush();
        siteDto.setStatusTime(LocalDateTime.now());
        Session session = getSession();
        Transaction tx1 = session.beginTransaction();
        session.update(siteMapper.toSiteEntity(siteDto));
        tx1.commit();
        session.close();
    }

    @Override
    public StatusIndexing getStatusIndexing(SiteDto siteDto) {
        return siteRepository.findById(siteDto.getId()).get().getStatus();
    }

    @Override
    public SiteDto findSite(String urlSite) {
        return siteMapper.toSiteDto(siteRepository.findByUrl(urlSite));
    }

    private Session getSession() {
        Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
        return session;
    }

}
