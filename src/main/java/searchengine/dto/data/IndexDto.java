package searchengine.dto.data;

import lombok.Data;

@Data

public class IndexDto {
    private Integer id;
    private PageDto pageDto;
    private LemmaDto lemmaDto;
    private Float rank;
}
