package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaEntity;
import searchengine.model.SiteEntity;

import java.util.List;

@Repository
public interface LemmaRepository extends JpaRepository<LemmaEntity, Integer> {
    @Transactional
    List<LemmaEntity> findAllBySiteEntity(SiteEntity site);
@Transactional
    void deleteAllBySiteEntity(SiteEntity site);
}
