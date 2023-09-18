package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.response.Response;
import searchengine.dto.searchs.SearchResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.PageIndexService;
import searchengine.services.SearchService;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final StatisticsService statisticsService;
    private final PageIndexService pageIndexService;
    private final SearchService searchService;

    @Autowired
    public ApiController(StatisticsService statisticsService, PageIndexService pageIndexService,
                         SearchService searchService) {
        this.statisticsService = statisticsService;
        this.pageIndexService = pageIndexService;
        this.searchService = searchService;
    }


    @GetMapping("/startIndexing")
    public @ResponseBody ResponseEntity<Response> startIndexing() throws Exception {
        Response response;
        if (pageIndexService.isActiveIndexing()) {
            log.info("Indexing is already running");
            response = new Response(false, "Индексация уже запущина");
        } else {
            response = new Response(true, "");
            log.info("Begin indexing");
            pageIndexService.startIndexedPagesAllSite();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Response> stopIndexing() {
        Response response;
        if (pageIndexService.isActiveIndexing()) {
            log.info("Stop indexing");
            response = new Response(true, "");
            pageIndexService.stopIndexedPagesAllSite();
        } else {
            log.info("Indexing is not running");
            response = new Response(false, "Индексация не запущена");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Response> indexOnePage(@RequestParam(name = "url") String urlPage) {
        if (urlPage.isEmpty()) {
            return new ResponseEntity<>(new Response(false, "Страница не указана"),HttpStatus.BAD_REQUEST);
        }
        if (!pageIndexService.isValidUrl(urlPage)) {
            return new ResponseEntity<>(
                    new Response(false, "Неверный формат адресса запрашиваемой страницы"),
                    HttpStatus.BAD_REQUEST);
        }
        if (!pageIndexService.isPresentUrlPage(urlPage)) {
            return new ResponseEntity<>(new Response(false,
                    "Указанная страница за пределами конфигурационного файла"),HttpStatus.BAD_REQUEST);
        } else {
            pageIndexService.indexOnePage(urlPage);
            return new ResponseEntity<>(new Response(true, ""),HttpStatus.OK);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "query", required = false, defaultValue = "") String query,
                                    @RequestParam(name = "site", required = false, defaultValue = "") String site,
                                    @RequestParam(name = "offset", required = false, defaultValue = "0") int offset,
                                    @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        if (query.isEmpty()) {
            Response response = new Response(false, "Задан пустой поисковой запрос");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        SearchResponse searchResponse;
        if (site.isEmpty()) {
            searchResponse = searchService.getResponseSearch(query, "", offset, limit);
        } else {
            searchResponse = searchService.getResponseSearch(query, site, offset, limit);
        }
        if (!searchResponse.isResult()) {
            Response response = new Response(false, "Не найдено");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(searchResponse, HttpStatus.OK);
    }
}
