//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package searchengine.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.stereotype.Component;
import searchengine.dto.data.SiteDto;
import searchengine.model.SiteEntity;

@Component
public class SiteMapperImpl implements SiteMapper {
    public SiteMapperImpl() {
    }

    public SiteEntity toSiteEntity(SiteDto siteDto) {
        if (siteDto == null) {
            return null;
        } else {
            SiteEntity site = new SiteEntity();
            site.setId(siteDto.getId());
            site.setStatus(siteDto.getStatus());
            site.setStatusTime(siteDto.getStatusTime());
            site.setLastError(siteDto.getLastError());
            site.setUrl(siteDto.getUrl());
            site.setName(siteDto.getName());
            return site;
        }
    }

    @Override
    public SiteDto toSiteDto(SiteEntity siteEntity) {
        if (siteEntity == null) {
            return null;
        } else {
            SiteDto siteDto = new SiteDto();
            siteDto.setId(siteEntity.getId());
            siteDto.setStatus(siteEntity.getStatus());
            siteDto.setStatusTime(siteEntity.getStatusTime());
            siteDto.setLastError(siteEntity.getLastError());
            siteDto.setUrl(siteEntity.getUrl());
            siteDto.setName(siteEntity.getName());
            return siteDto;
        }

    }

    public List<SiteEntity> toSiteEntityList(List<SiteDto> siteDtoList) {
        if (siteDtoList == null) {
            return null;
        } else {
            List<SiteEntity> list = new ArrayList(siteDtoList.size());
            Iterator var3 = siteDtoList.iterator();

            while(var3.hasNext()) {
                SiteDto siteDto = (SiteDto)var3.next();
                list.add(this.toSiteEntity(siteDto));
            }

            return list;
        }
    }

    public List<SiteDto> toSiteDtoList(List<SiteEntity> siteList) {
        if (siteList == null) {
            return null;
        } else {
            List<SiteDto> list = new ArrayList(siteList.size());
            Iterator var3 = siteList.iterator();

            while(var3.hasNext()) {
                SiteEntity site = (SiteEntity)var3.next();
                list.add(this.toSiteDto(site));
            }

            return list;
        }
    }



}
