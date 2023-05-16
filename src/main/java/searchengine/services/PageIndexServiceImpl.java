package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.companets.ServiceSite;
import searchengine.companets.extractPageOnSite.ParserPage;
import searchengine.config.ConfigOptions;
import searchengine.config.SiteConfig;
import searchengine.dto.data.SiteDto;
import searchengine.dto.response.Response;
import searchengine.model.StatusIndexing;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PageIndexServiceImpl  implements PageIndexService{

    private ConfigOptions sitesList;
    private ServiceSite serviceSite;
    private ConfigOptions configOptions;
    private ParserPage parserPage;
    @Autowired
    public PageIndexServiceImpl(ConfigOptions sitesList, ServiceSite serviceSite, ConfigOptions configOptions, ParserPage parserPage) {
        this.sitesList = sitesList;
        this.serviceSite = serviceSite;
        this.configOptions = configOptions;
        this.parserPage = parserPage;
    }

    @Override
    public Response startIndexPage(){

        List<SiteDto> siteDTOList = getSiteFromConfig();
        serviceSite.allSaveSites(siteDTOList);

        siteDTOList.forEach(siteDto -> {
           parserPage.startParserSite(siteDto);
        });
        return new Response(false,"");
    };

    private List<SiteDto> getSiteFromConfig(){
        List<SiteDto> sitesDTOList = new ArrayList<>();
        int i = 1;
        for (SiteConfig siteConfig : sitesList.getSites()) {
            SiteDto siteDTO = new SiteDto();
            siteDTO.setId(i);
            siteDTO.setUrl(siteConfig.getUrl());
            siteDTO.setName(siteConfig.getName());
            siteDTO.setStatus(StatusIndexing.INDEXING);
            siteDTO.setStatusTime(LocalDateTime.now());
            siteDTO.setLastError(null);
            sitesDTOList.add(siteDTO);
            i++;
        }
        return sitesDTOList;
    }

}
