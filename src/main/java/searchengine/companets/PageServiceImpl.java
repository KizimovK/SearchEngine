package searchengine.companets;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.config.ConfigOptions;
import searchengine.dto.data.PageDto;
import searchengine.mapping.PageMapper;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Component
public class PageServiceImpl implements PageService {
    private final SiteRepository siteRepository;
    private final PageRepository pageRepository;
    private final ConfigOptions configOptions;
    private final PageMapper pageMapper;

    @Autowired
    public PageServiceImpl(SiteRepository siteRepository, PageRepository pageRepository,
                           ConfigOptions configOptions, PageMapper pageMapper) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.configOptions = configOptions;
        this.pageMapper = pageMapper;
    }

    public PageDto getPageDto(String urlSite) throws IOException {
        int code = Jsoup.connect(urlSite).userAgent(configOptions.getUserAgent()).
                referrer(configOptions.getReferrer()).response().statusCode();
        String content = Jsoup.connect(urlSite).userAgent(configOptions.getUserAgent()).
                referrer(configOptions.getReferrer()).get().toString();
        PageDto pageDto = new PageDto();

        pageDto.setPath(getPathPage(urlSite));

        //pageDto.setSiteDto(siteDto);
        pageDto.setCode(code);
        pageDto.setContent(content);
        return pageDto;
    }

    public String getPathPage(String urlPage) {
        URL url = null;
        try {
            url = new URL(urlPage);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if (urlPage.isEmpty()) {
            return "";
        }
        return url.getFile();
    }

    public void savePage(PageDto pageDto) throws IOException {
        pageRepository.save(pageMapper.mapperToPage(pageDto));
    }

    public void allSavePage(List<PageDto> pageDtoList) {
        pageRepository.saveAll(pageMapper.mappingToListPage(pageDtoList));
    }

}

