package searchengine.dto.data;

import lombok.Data;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
@Data
public class IndexDto {
    private Integer id;
    private PageDto pageDto;
    private LemmaDto lemmaDto;
    private Float rank;
}
