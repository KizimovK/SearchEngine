package searchengine.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.response.Response;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final StatisticsService statisticsService;


    public ApiController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }
    @GetMapping("/startIndexing")
    public @ResponseBody ResponseEntity<Response> startIndexing(){
        Response result = new Response(false,"Индексация уже запущена");
        return  new ResponseEntity<>(result, HttpStatus.OK);
    }
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }
}
