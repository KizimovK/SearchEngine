package searchengine.mapping;

import org.mapstruct.Mapper;
import searchengine.dto.data.SiteDto;
import searchengine.model.SiteEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SiteMapper {


    SiteEntity toSiteEntity(SiteDto siteDto);
    SiteDto toSiteDto(SiteEntity siteEntity);


    List<SiteEntity> toSiteEntityList(List<SiteDto> siteDtoList);
    List<SiteDto> toSiteDtoList(List<SiteEntity> siteList);


}
