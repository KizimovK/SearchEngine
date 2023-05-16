package searchengine.companets;

import searchengine.dto.data.PageDto;

import java.io.IOException;
import java.util.List;


public interface PageService {


    PageDto getPageDto(String urlPage) throws IOException;

    String getPathPage(String urlPage);

     void savePage(PageDto pageDto) throws IOException;

    void allSavePage(List<PageDto> pageDtoList);


}
