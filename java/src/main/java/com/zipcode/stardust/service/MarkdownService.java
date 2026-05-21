package com.zipcode.stardust.service;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Service; 


@Service
public class MarkdownService {

    private final Parser parser = Parser.builder().build(); 
    private final HtmlRenderer renderer = HtmlRenderer.builder().build(); 

    private final PolicyFactory sanitizer = Sanitizers.FORMATTING
    .and(Sanitizers.BLOCKS)
    .and(Sanitizers.LINKS);

    public String renderMarkdown(String markdown) { 
        if (markdown == null || markdown.isBlank()) { 
            return "";
        }

        Node document = parser.parse(markdown); 
        String unsafeHtml = renderer.render(document);

        return sanitizer.sanitize(unsafeHtml);
    }
    
}
