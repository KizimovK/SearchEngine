package searchengine.mapping;

import org.mapstruct.Mapper;
import searchengine.dto.data.SiteDto;
import searchengine.model.Site;

import java.util.List;

//@Mapper(componentModel = "spring")
public interface SiteMapper {


    Site mappingToSite(SiteDto siteDto);


    List<Site> mappingToSiteList(List<SiteDto> siteDtoList);
    List<SiteDto> mappingToSiteDtoList(List<Site> siteList);


}
