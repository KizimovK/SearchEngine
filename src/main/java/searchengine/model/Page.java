package searchengine.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "pages")
@RequiredArgsConstructor
@Setter
@Getter
/*
    Сущьность Page, Таблица Pages
    Связь между таблицой Sites, через поле site_id, один-ко-одному,
    Поле path является индексом
 */
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;
    @Column(name = "path",columnDefinition = "VARCHAR(255)", unique = true, nullable = false)
    private String path;
    private Integer code;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

}
