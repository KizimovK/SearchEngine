package searchengine.dto.data;

import lombok.Data;
import searchengine.model.Site;

import javax.persistence.*;
@Data
public class PageDto {
    //private Integer id;
    private SiteDto SiteDto;
    private String path;
    private Integer code;
    private String content;
}
