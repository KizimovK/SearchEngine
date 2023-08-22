package searchengine.services;

import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;

import java.io.IOException;
import java.util.List;


public interface PageService {

    PageDto savePage(PageDto pageDto) throws IOException;

    List<PageDto> allSavePage(List<PageDto> pageDtoList);

    void dropPagesSite(SiteDto siteDto);

}
