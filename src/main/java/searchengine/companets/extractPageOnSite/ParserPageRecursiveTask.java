package searchengine.companets.extractPageOnSite;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.companets.PageService;
import searchengine.config.ConfigOptions;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

@RequiredArgsConstructor

public class ParserPageRecursiveTask extends RecursiveTask<CopyOnWriteArrayList> {


    private final String urlSite;
    private final SiteDto siteDto;
    private final CopyOnWriteArrayList<String> allPagesSite;
    private final CopyOnWriteArrayList<PageDto> listPageDto;

    private  final ConfigOptions configOptions;

    private  final PageService pageService ;


@Override
    protected CopyOnWriteArrayList<PageDto> compute() {
        try {
            Thread.sleep(100);
            Document document = null;
            try {
                Thread.sleep(100);
                document = Jsoup.connect(urlSite).userAgent(configOptions.getUserAgent())
                        .referrer(configOptions.getReferrer()).get();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
            List<ParserPageRecursiveTask> taskList = new CopyOnWriteArrayList<>();
            String urlPage;
            Elements elements = document.select("a[href]");
            ParserPageRecursiveTask task;
            PageDto pageDto = getPageDtoDocument(document);
            if (!listPageDto.contains(pageDto)){
                 listPageDto.add(pageDto);
            }
            for (Element element : elements) {
                urlPage = element.absUrl("href");
                if (checkUrlPage(urlPage) &&
                        !urlPage.isEmpty() &&
                        urlPage.startsWith(element.baseUri()) &&
                        !allPagesSite.contains(urlPage) &&
                        !urlPage.contains("#") &&
                        urlPage.endsWith("/")) {
                    allPagesSite.add(urlPage);
                    task = new ParserPageRecursiveTask(urlPage, siteDto, allPagesSite, listPageDto,configOptions,pageService);
                    task.fork();
                    taskList.add(task);
                }
            }
        taskList.forEach(ForkJoinTask::join);
        } catch (IOException |InterruptedException e) {
            throw new RuntimeException(e);
        }


    return listPageDto;
    }
    private PageDto getPageDtoDocument(Document element) throws IOException {
        int code = element.connection().response().statusCode();
        String content = element.outerHtml();
        PageDto pageDto = new PageDto();
        pageDto.setPath(pageService.getPathPage(urlSite));
        pageDto.setSiteDto(siteDto);
        pageDto.setCode(code);
        pageDto.setContent(content);
        return pageDto;
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
}
