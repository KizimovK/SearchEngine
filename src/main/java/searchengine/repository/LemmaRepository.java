package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaEntity;
import searchengine.model.SiteEntity;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<LemmaEntity, Integer> {

    @Transactional
    @Modifying
    @Query(value = "UPDATE lemmas l SET l.frequency = l.frequency + 1 WHERE l.id = :id", nativeQuery = true)
    int updateLemma(@Param("id") int id);

    @Transactional
    List<LemmaEntity> findAllBySiteEntity(SiteEntity siteEntity);

    //    @Modifying
//    @Query(value = "INSERT INTO LemmaEntity l (l.lemma, l.s, l.frequency) select (:lemma,:idSite,:frequency)",nativeQuery = true)
//    int insertLemma(@Param("lemma")String lemma,@Param("site_id") int idSite, @Param("frequency") int frequency);
    @Transactional
    LemmaEntity findFirstById(int idLemma);

    @Transactional
    void deleteAllBySiteEntity(SiteEntity site);

    @Transactional
    int countBySiteEntity(SiteEntity siteEntity);

    @Transactional
    @Query(value = "SELECT * FROM lemmas l WHERE l.lemma IN :lemmasQuery AND l.site_id = :siteId", nativeQuery = true)
    List<LemmaEntity> findLemmasListBySite(@Param("lemmasQuery") List<String> lemmasQuery, @Param("siteId") int siteId);

}
