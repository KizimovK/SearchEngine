package searchengine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pages", indexes = @Index(columnList = "path"))
@RequiredArgsConstructor
@Setter
@Getter

public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "path", columnDefinition = "VARCHAR(255)", nullable = false)
    private String path;
    private Integer code;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "site_id", nullable = false, referencedColumnName = "id")
    private SiteEntity siteEntity;

}
