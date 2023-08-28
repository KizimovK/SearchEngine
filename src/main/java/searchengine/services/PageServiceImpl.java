package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.PageMapper;
import searchengine.mapping.SiteMapper;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.util.List;

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

    public PageDto savePage(PageDto pageDto) throws IOException {
        return pageMapper.toPageDto(pageRepository.save(pageMapper.toPageEntity(pageDto)));
    }

    public List<PageDto> allSavePage(List<PageDto> pageDtoList) {
        pageRepository.flush();
        List<PageEntity> pageEntityList = pageRepository.saveAll(pageMapper.toListPageEntity(pageDtoList));
        return pageMapper.toListPageDto(pageEntityList);
    }

    @Override
    public void dropPagesSite(SiteDto siteDto) {
        log.info("start pages delete from ".concat(siteDto.getUrl()));
        SiteEntity site = siteRepository.findByUrl(siteMapper.toSiteEntity(siteDto).getUrl());
        pageRepository.deleteBySiteEntity(site);
    }

}

