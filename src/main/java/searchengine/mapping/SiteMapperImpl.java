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
import searchengine.model.Site;

@Component
public class SiteMapperImpl implements SiteMapper {
    public SiteMapperImpl() {
    }

    public Site mappingToSite(SiteDto siteDto) {
        if (siteDto == null) {
            return null;
        } else {
            Site site = new Site();
            //site.setId(siteDto.getId());
            site.setStatus(siteDto.getStatus());
            site.setStatusTime(siteDto.getStatusTime());
            site.setLastError(siteDto.getLastError());
            site.setUrl(siteDto.getUrl());
            site.setName(siteDto.getName());
            return site;
        }
    }

    public List<Site> mappingToSiteList(List<SiteDto> siteDtoList) {
        if (siteDtoList == null) {
            return null;
        } else {
            List<Site> list = new ArrayList(siteDtoList.size());
            Iterator var3 = siteDtoList.iterator();

            while(var3.hasNext()) {
                SiteDto siteDto = (SiteDto)var3.next();
                list.add(this.mappingToSite(siteDto));
            }

            return list;
        }
    }

    public List<SiteDto> mappingToSiteDtoList(List<Site> siteList) {
        if (siteList == null) {
            return null;
        } else {
            List<SiteDto> list = new ArrayList(siteList.size());
            Iterator var3 = siteList.iterator();

            while(var3.hasNext()) {
                Site site = (Site)var3.next();
                list.add(this.siteToSiteDto(site));
            }

            return list;
        }
    }

    protected SiteDto siteToSiteDto(Site site) {
        if (site == null) {
            return null;
        } else {
            SiteDto siteDto = new SiteDto();
            siteDto.setStatus(site.getStatus());
            siteDto.setStatusTime(site.getStatusTime());
            siteDto.setLastError(site.getLastError());
            siteDto.setUrl(site.getUrl());
            siteDto.setName(site.getName());
            return siteDto;
        }
    }
}
