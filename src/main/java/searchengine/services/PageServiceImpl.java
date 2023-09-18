package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.PageMapper;
import searchengine.mapping.SiteMapper;
import searchengine.model.PageEntity;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

@Slf4j
@Service

public class PageServiceImpl implements PageService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final PageMapper pageMapper;
    private final SiteMapper siteMapper;

    @Autowired
    public PageServiceImpl(SiteRepository siteRepository, PageRepository pageRepository,
                           PageMapper pageMapper, SiteMapper siteMapper) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.pageMapper = pageMapper;
        this.siteMapper = siteMapper;
    }

    @Override
    public PageDto savePage(PageDto pageDto, SiteDto siteDto) {
        PageEntity pageEntity = pageMapper.toPageEntity(pageDto);
        pageEntity.setSiteEntity(siteMapper.toSiteEntity(siteDto));
        return pageMapper.toPageDto(pageRepository.save(pageEntity));
    }

}

