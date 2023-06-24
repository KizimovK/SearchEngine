package searchengine.companets;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.ConfigOptions;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.PageMapper;
import searchengine.mapping.SiteMapper;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Slf4j
@Component
public class PageServiceImpl implements PageService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final ConfigOptions configOptions;
    private final PageMapper pageMapper;
    private final SiteMapper siteMapper;

    @Autowired
    public PageServiceImpl(SiteRepository siteRepository, PageRepository pageRepository,
                           ConfigOptions configOptions, PageMapper pageMapper, SiteMapper siteMapper) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.configOptions = configOptions;
        this.pageMapper = pageMapper;
        this.siteMapper = siteMapper;
    }

//    public PageDto getPageDto(String urlSite) throws IOException {
//        int code = Jsoup.connect(urlSite).userAgent(configOptions.getUserAgent()).
//                referrer(configOptions.getReferrer()).response().statusCode();
//        String content = Jsoup.connect(urlSite).userAgent(configOptions.getUserAgent()).
//                referrer(configOptions.getReferrer()).get().toString();
//        PageDto pageDto = new PageDto();
//
//        pageDto.setPath(getPathPage(urlSite));
//
//        //pageDto.setSiteDto(siteDto);
//        pageDto.setCode(code);
//        pageDto.setContent(content);
//        return pageDto;
//    }



    public void savePage(PageDto pageDto) throws IOException {
        pageRepository.save(pageMapper.mapperToPage(pageDto));
    }

    public void allSavePage(List<PageDto> pageDtoList) {
        List<Page> pageList = pageMapper.mappingToListPage(pageDtoList);
        pageRepository.saveAllAndFlush(pageList);
    }

    @Override
    public void dropPagesSite(SiteDto siteDto) {
        log.info("start pages delete from ".concat(siteDto.getUrl()));
        Site site = siteRepository.findByUrl(siteMapper.mappingToSite(siteDto).getUrl());
        pageRepository.deleteAllBySite(site);
    }

}

