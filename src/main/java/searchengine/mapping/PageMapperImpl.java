

package searchengine.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.data.PageDto;
import searchengine.model.PageEntity;

@Component
public class PageMapperImpl implements PageMapper {

    private final SiteMapper siteMapper;
    @Autowired
    public PageMapperImpl(SiteMapper siteMapper) {
        this.siteMapper = siteMapper;
    }

    public PageEntity toPageEntity(PageDto pageDto) {
        if (pageDto == null) {
            return null;
        } else {
            PageEntity page = new PageEntity();
            page.setPath(pageDto.getPath());
            page.setCode(pageDto.getCode());
            page.setContent(pageDto.getContent());
            page.setSiteEntity(siteMapper.toSiteEntity(pageDto.getSiteDto()));
            return page;
        }
    }

    @Override
    public PageDto toPageDto(PageEntity pageEntity) {
         if (pageEntity == null) {
            return null;
        } else {
            PageDto pageDto = new PageDto();
            pageDto.setPath(pageEntity.getPath());
            pageDto.setCode(pageEntity.getCode());
            pageDto.setContent(pageEntity.getContent());
            pageDto.setSiteDto(siteMapper.toSiteDto(pageEntity.getSiteEntity()));
            return pageDto;
        }
    }

    public List<PageEntity> toListPageEntity(List<PageDto> pageDtoList) {
        if (pageDtoList == null) {
            return null;
        } else {
            List<PageEntity> list = new ArrayList(pageDtoList.size());
            Iterator var3 = pageDtoList.iterator();
            while(var3.hasNext()) {
                PageDto pageDto = (PageDto)var3.next();
                list.add(this.toPageEntity(pageDto));
            }
            return list;
        }
    }
}
