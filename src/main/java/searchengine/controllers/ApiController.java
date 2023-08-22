package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.response.Response;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.PageIndexService;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final StatisticsService statisticsService;
    private final PageIndexService pageIndexService;

    @Autowired
    public ApiController(StatisticsService statisticsService, PageIndexService pageIndexService) {
        this.statisticsService = statisticsService;
        this.pageIndexService = pageIndexService;
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
    public ResponseEntity<Response> indexOnePage(@RequestParam(name = "url") String urlPage){
        Response response;
        if  (urlPage.isEmpty()){
            response = new Response(false, "Страница не указана");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if (!pageIndexService.isPresentUrlPage(urlPage)) {
            response = new Response(false, "Указанная страница за пределами конфигурационного файла");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        pageIndexService.indexOnePage(urlPage);
        response = new Response(true,"");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
