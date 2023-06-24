package searchengine.dto.data;

import lombok.Data;
import searchengine.model.StatusIndexing;

import java.time.LocalDateTime;

@Data

public class SiteDto {
    private Integer id;
    private StatusIndexing status;
    private LocalDateTime statusTime;
    private String lastError;
    private String url;
    private String name;
}
