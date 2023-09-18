package searchengine.dto.data;

import lombok.Data;

@Data
public class PageDto {
    private Integer id;
    private String path;
    private Integer code;
    private String content;
}
