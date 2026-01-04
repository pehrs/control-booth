package com.pehrs.controlbooth.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/about")
@Slf4j
public class AboutController {

  static class ImageAttributeProvider implements AttributeProvider {
    @Override
    public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
      if (node instanceof Link) {
        attributes.put("target", "_");
      }
    }
  }

  @GetMapping(value = "/html", produces = "text/html")
  public String html() throws URISyntaxException, IOException {

    try {
      String md = Files.readString(
          Paths.get(getClass().getClassLoader().getResource("about-rendered.md").toURI()));

      Parser parser = Parser.builder().build();
      Node document = parser.parse(md);

      HtmlRenderer renderer = HtmlRenderer.builder()
          .attributeProviderFactory(new AttributeProviderFactory() {
            public AttributeProvider create(AttributeProviderContext context) {
              return new ImageAttributeProvider();
            }
          })
          .build();
      String html = renderer.render(document);

      return html;
    } catch (Exception e) {
      return """
          <h1>About</h1>
          <p>Please run the ./scripts/render-about.sh to generate the about markdown.</p>
          """;
    }
  }
}
