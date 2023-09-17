package searchengine.companets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.SiteMapper;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
public class CleanerOfDataSite {
    private final SiteRepository siteRepository;
    private final SiteMapper siteMapper;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;


    public CleanerOfDataSite(SiteRepository siteRepository, SiteMapper siteMapper, PageRepository pageRepository, LemmaRepository lemmaRepository, IndexRepository indexRepository) {
        this.siteRepository = siteRepository;
        this.siteMapper = siteMapper;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

    public void cleaning(SiteDto siteDto) {
        log.info("Begin remove on site " + siteDto.getUrl());
        SiteEntity site = siteRepository.findByUrl(siteDto.getUrl());
        if (site == null) {
            log.info("There is no data related to the site " + siteDto.getUrl());
            return;
        }
        List<PageEntity> pageEntityList = pageRepository.findAllBySiteEntity(site);
        log.info("\tRemove index " + siteDto.getUrl());
        removeIndexesAllPage(pageEntityList);
        pageRepository.deleteAllBySiteEntity(site);
        lemmaRepository.deleteAllBySiteEntity(site);
        log.info("\tRemove page on site " + siteDto.getUrl());

        siteRepository.delete(site);
        log.info("\tThe process of deleting site" + siteDto.getUrl() + " data is completed!");
    }

    private void removeIndexesAllPage(List<PageEntity> pageEntityList) {
        indexRepository.flush();
        if (pageEntityList != null) {
            pageEntityList.forEach(pageEntity -> {
                indexRepository.deleteAllByPageEntity(pageEntity);
            });
        }
    }

    public void removeOnePage(String urlPage, SiteDto siteDto) {
        PageEntity pageEntity =
                pageRepository.findByPathAndSiteEntity(getPathPage(urlPage), siteMapper.toSiteEntity(siteDto));
        if (pageEntity != null) {
            indexRepository.deleteAllByPageEntity(pageEntity);
            pageRepository.delete(pageEntity);
        }
    }

    private String getPathPage(String urlPage) {
        if (urlPage.isEmpty()) {
            return "";
        }
        URL url = null;
        try {
            url = new URL(urlPage);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url.getPath();
    }


}

