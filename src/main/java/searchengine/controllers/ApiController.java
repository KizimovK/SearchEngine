package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
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
            pageIndexService.startIndexPage();
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Response> stopIndexing() {
        Response response;
        if (pageIndexService.isActiveIndexing()) {
            log.info("Stop indexing");
            response = new Response(true, "");
            pageIndexService.stopIndexPage();
        } else {
            log.info("Indexing is not running");
            response = new Response(false, "Индексация не запущена");
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
