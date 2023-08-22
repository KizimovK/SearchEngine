package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.IndexEntity;
import searchengine.model.PageEntity;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<IndexEntity, Integer> {
    @Transactional
    void deleteAllByPageEntity(PageEntity pageEntity);
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO index_lemma (page_id, lemma_id, rank_index) " +
            "VALUES (?,?, ?)",nativeQuery = true)
    int insertIndex(@Param("page_id") int idPage, @Param("lemma_id") int idLemma, @Param("rank_index") float rank);
    @Transactional
    List<IndexEntity> findAllByPageEntity(PageEntity pageEntity);
}
