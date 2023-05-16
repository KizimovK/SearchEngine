package searchengine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "pages", indexes = @Index(columnList = "path", unique = true))
@RequiredArgsConstructor
@Setter
@Getter

public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "path",columnDefinition = "VARCHAR(255)", nullable = false)
    private String path;
    private Integer code;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;
}
