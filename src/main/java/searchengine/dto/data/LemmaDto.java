package searchengine.dto.data;

import lombok.Data;

@Data
public class LemmaDto {
    private int id;
    private SiteDto siteDto;
    private String lemma;
    private int frequency;
}
