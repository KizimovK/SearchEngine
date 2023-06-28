package searchengine.dto.data;

import lombok.Data;

@Data
public class LemmaDto {
    private Integer id;
    private SiteDto siteDto;
    private String lemma;
    private Integer frequency;
}
