package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.response.Response;
import searchengine.services.PageIndexService;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;
    private final PageIndexService pageIndexService;

    @Autowired
    public ApiController(StatisticsService statisticsService, PageIndexService pageIndexService){
        this.statisticsService = statisticsService;
        this.pageIndexService = pageIndexService;

    }


    @GetMapping("/startIndexing")
    public @ResponseBody ResponseEntity<Response> startIndexing(){
        Response response = new Response(false,"Индексация уже запущена");
        pageIndexService.startIndexPage();
        return  new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
