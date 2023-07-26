package searchengine.companets;

import org.springframework.stereotype.Component;
import searchengine.companets.extractPageOnSite.ParserPage;
import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;
import searchengine.repository.PageRepository;

import java.util.List;



public interface IndexingSite {
   void taskIndexingSite(SiteDto siteDto);
   void taskIndexingOnePage(String urlPage);
}
