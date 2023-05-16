

package searchengine.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.data.PageDto;
import searchengine.model.Page;

@Component
public class PageMapperImpl implements PageMapper {

    private final SiteMapper siteMapper;
    @Autowired
    public PageMapperImpl(SiteMapper siteMapper) {
        this.siteMapper = siteMapper;
    }

    public Page mapperToPage(PageDto pageDto) {
        if (pageDto == null) {
            return null;
        } else {
            Page page = new Page();
            page.setPath(pageDto.getPath());
            page.setCode(pageDto.getCode());
            page.setContent(pageDto.getContent());
            page.setSite(siteMapper.mappingToSite(pageDto.getSiteDto()));
            return page;
        }
    }

    public List<Page> mappingToListPage(List<PageDto> pageDtoList) {
        if (pageDtoList == null) {
            return null;
        } else {
            List<Page> list = new ArrayList(pageDtoList.size());
            Iterator var3 = pageDtoList.iterator();

            while(var3.hasNext()) {
                PageDto pageDto = (PageDto)var3.next();
                list.add(this.mapperToPage(pageDto));
            }

            return list;
        }
    }
}
