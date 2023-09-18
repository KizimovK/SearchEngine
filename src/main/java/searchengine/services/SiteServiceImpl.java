package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.SiteMapper;
import searchengine.model.StatusIndexing;
import searchengine.repository.SiteRepository;

import javax.persistence.EntityManagerFactory;
import java.net.MalformedURLException;
import java.net.URL;
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
    public SiteDto saveSite(SiteDto siteDto) {
        return siteMapper.toSiteDto(siteRepository.saveAndFlush(siteMapper.toSiteEntity(siteDto)));
    }

    @Override
    public List<SiteDto> findAllSite() {
        siteRepository.flush();
        return new ArrayList<>(siteMapper.toSiteDtoList(siteRepository.findAll()));
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
        return entityManagerFactory.unwrap(SessionFactory.class).openSession();
    }

    @Override
    public String getBaseUrlSite(String urlPage) {
        if (urlPage.isEmpty()) {
            return "";
        }
        URL url;
        try {
            url = new URL(urlPage);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url.getHost();
    }

    @Override
    public SiteDto getSiteFromConfig(SiteConfig siteConfig) {
        SiteDto siteDTO = new SiteDto();
        siteDTO.setUrl(siteConfig.getUrl());
        siteDTO.setName(siteConfig.getName());
        siteDTO.setStatus(StatusIndexing.INDEXING);
        siteDTO.setStatusTime(LocalDateTime.now());
        siteDTO.setLastError(null);
        return siteDTO;
    }

}
