package searchengine.companets;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import searchengine.dto.data.SiteDto;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.util.List;

@Slf4j
@Component
public class CleanerOfDataSte {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;


    public CleanerOfDataSte(SiteRepository siteRepository, PageRepository pageRepository, LemmaRepository lemmaRepository, IndexRepository indexRepository) {
        this.siteRepository = siteRepository;
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
        removeIndexes(pageEntityList);
        pageRepository.deleteAllBySiteEntity(site);
        lemmaRepository.deleteAllBySiteEntity(site);
        log.info("\tRemove page on site " + siteDto.getUrl());

        siteRepository.delete(site);
        log.info("\tThe process of deleting site" + siteDto.getUrl() + " data is completed!");
    }

    private void removeIndexes(List<PageEntity> pageEntityList) {
        indexRepository.flush();
        if (pageEntityList != null) {
            pageEntityList.forEach(pageEntity -> {
                indexRepository.deleteAllByPageEntity(pageEntity);
            });
        }
    }


}

