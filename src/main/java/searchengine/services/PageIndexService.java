package searchengine.services;

public interface PageIndexService {
    void startIndexedPagesAllSite() throws Exception;
    boolean isActiveIndexing();
    void stopIndexedPagesAllSite();
    boolean isPresentUrlPage(String urlPage);
    void indexOnePage(String urlPage);
}
