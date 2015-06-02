package com.rarchives.ripme.ripper.rippers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rarchives.ripme.ripper.AbstractHTMLRipper;
import com.rarchives.ripme.utils.Http;

public class IntimlifeRipper extends AbstractHTMLRipper {

    public IntimlifeRipper(URL url) throws IOException {
        super(url);
    }

    @Override
    public String getHost() {
        return "intimlife";
    }
    @Override
    public String getDomain() {
        return "intimlife.com";
    }

    @Override
    public String getGID(URL url) throws MalformedURLException {
        Pattern p; Matcher m;


        p = Pattern.compile("^.*intimlife.com/details/([a-zA-Z0-9\\-_]+)/.*$");
        m = p.matcher(url.toExternalForm());
        if (m.matches()) {
            return m.group(1);
        }

        throw new MalformedURLException(
                "Expected intimlife.com gallery format: "
                        + "intimlife.com/detail/ID/photos"
                        + " Got: " + url);
    }

    @Override
    public Document getFirstPage() throws IOException {
        return Http.url(this.url).get();
    }

    @Override
    public Document getNextPage(Document doc) throws IOException {
        Elements loadMore = doc.select(".photoblock > a");
        if (loadMore.size() == 0) {
            throw new IOException("No next page found");
        }
        if (!sleep(1000)) {
            throw new IOException("Interrupted while waiting for next page");
        }
        String nextPage = "http://intimlife.com/" + loadMore.get(0).attr("href");
        return Http.url(nextPage).get();
    }

    @Override
    public List<String> getURLsFromPage(Document page) {
        List<String> imageURLs = new ArrayList<String>();
        for (Element thumb : page.select(".img-wrapper")) {
            String image = thumb.attr("src");
            imageURLs.add(image);
        }
        return imageURLs;
    }

    @Override
    public void downloadURL(URL url, int index) {
        addURLToDownload(url, getPrefix(index));
    }

}
