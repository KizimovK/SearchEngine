package searchengine.companets.extractPageOnSite;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.companets.PageService;
import searchengine.companets.SiteService;
import searchengine.config.ConfigOptions;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

@RequiredArgsConstructor
@Slf4j

public class ParserPageRecursiveTask extends RecursiveTask<CopyOnWriteArrayList> {


    private final String urlSite;
    private final SiteDto siteDto;
    private final CopyOnWriteArrayList<String> allPagesSite;
    private final CopyOnWriteArrayList<PageDto> listPageDto;

    private final ConfigOptions configOptions;

    private final PageService pageService;
    private final SiteService siteService;


    @Override
    protected CopyOnWriteArrayList<PageDto> compute() {
        Document document = null;
        try {
            Thread.sleep(150);
            document = Jsoup.connect(urlSite).userAgent(configOptions.getUserAgent())
                    .referrer(configOptions.getReferrer()).get();
            List<ParserPageRecursiveTask> taskList = new CopyOnWriteArrayList<>();
            String urlPage;
            Elements elements = document.select("a[href]");
            ParserPageRecursiveTask task;
            PageDto pageDto = getPageDtoDocument(document);
            listPageDto.add(pageDto);
            siteService.updateStatusTime(siteDto);
            for (Element element : elements) {
                urlPage = element.absUrl("href");
                if (checkUrlPage(urlPage) &&
                        !urlPage.isEmpty() &&
                        urlPage.startsWith(element.baseUri()) &&
                        !allPagesSite.contains(urlPage) &&
                        !urlPage.contains("#")
                ) {
                    allPagesSite.add(urlPage);
                    task = new ParserPageRecursiveTask(urlPage, siteDto, allPagesSite, listPageDto,
                            configOptions, pageService, siteService);
                    task.fork();
                    taskList.add(task);
                }
            }
            taskList.forEach(ForkJoinTask::join);
        } catch (HttpStatusException e) {
            listPageDto.add(getPageDtoHttpStatusException(e));
        } catch (UnknownHostException e){
            connectException("Unknown host exception or not connecting url" + e.getMessage());
        } catch (SocketTimeoutException e){
            connectException("Read timed out or not connecting.");
        }
        catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return listPageDto;
    }

    private PageDto getPageDtoDocument(Document document) {
        int code = document.connection().response().statusCode();
        String content = document.outerHtml();
        PageDto pageDto = new PageDto();
        pageDto.setPath(getPathPage(document.baseUri()));
        pageDto.setSiteDto(siteDto);
        pageDto.setCode(code);
        pageDto.setContent(content);
        return pageDto;
    }
    private PageDto getPageDtoHttpStatusException(HttpStatusException e){
        String lastError = e.getMessage();
        int code = e.getStatusCode();
        String urlPage = e.getUrl();
        PageDto pageDto = new PageDto();
        pageDto.setPath(getPathPage(urlPage));
        pageDto.setSiteDto(siteDto);
        pageDto.setCode(code);
        pageDto.setContent(null);
        log.error(lastError);
        siteService.updateStatusSite(siteDto, lastError, StatusIndexing.INDEXED);
        return pageDto;
    }
    private void connectException(String error){
        log.error(error);
        siteService.updateStatusSite(siteDto, error, StatusIndexing.FAILED);
    }
    private boolean checkUrlPage(String urlPage) {
        return !urlPage.contains("#")
                && !urlPage.contains("?")
                && !urlPage.contains("%")
                && !urlPage.contains(".pdf")
                && !urlPage.contains(".jpg")
                && !urlPage.contains(".doc")
                && !urlPage.contains(".docx")
                && !urlPage.contains(".jpeg");
    }

    private String getPathPage(String urlPage) {
        URL url = null;
        try {
            url = new URL(urlPage);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        if (urlPage.isEmpty()) {
            return "";
        }
        return url.getPath();
    }

}
