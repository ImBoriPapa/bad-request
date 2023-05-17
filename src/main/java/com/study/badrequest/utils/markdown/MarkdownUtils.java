package com.study.badrequest.utils.markdown;


import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;


public class MarkdownUtils {

    public static String parseMarkdownToHtml(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return sanitizeHtml(renderer.render(document));
    }

    public static String markdownToPlainText(String markdown) {
        String sanitizedHtml = parseMarkdownToHtml(markdown);
        return Jsoup.parse(sanitizedHtml).text();
    }

    private static String sanitizeHtml(String html) {
        return Jsoup.clean(html, Safelist.relaxed());
    }



}
