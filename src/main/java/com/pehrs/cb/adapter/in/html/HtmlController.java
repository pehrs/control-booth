package com.pehrs.cb.adapter.in.html;


import com.pehrs.cb.core.domain.CatalogEntity;
import com.pehrs.cb.core.service.CatalogEntityServiceImpl;
import com.pehrs.cb.port.in.CatalogEntityService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/html")
@Slf4j
@AllArgsConstructor
public class HtmlController {
    // Use lynx or w3m to browse these pages.

    private freemarker.template.Configuration freemarkerConfig;

    private CatalogEntityService entityService;


    private Map<String, Object> createContext(Authentication authentication) {
        String username = authentication.getName();

        // Create the root hash. We use a Map here, but it could be a JavaBean too.
        Map<String, Object> root = new HashMap<>();
        root.put("user", username);
        return root;
    }

    private String renderHtml(String tempName, Authentication authentication) {
        return renderHtml(tempName, authentication, Map.of());
    }
    private String renderHtml(String tempName, Authentication authentication, Map<String, Object> xtraCtx) {
        try {
            Template temp = freemarkerConfig.getTemplate(tempName);
            Map<String, Object> ctx = createContext(authentication);
            ctx.putAll(xtraCtx);

            Writer out = new StringWriter();
            temp.process(ctx, out);
            return out.toString();
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/index")
    public ResponseEntity<String> index(Authentication authentication) {
        String html = renderHtml("index.ftlh", authentication);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping("/about")
    public ResponseEntity<String> username(Authentication authentication) {
        String html = renderHtml("about.ftlh", authentication);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

    @GetMapping("/fm-test")
    public ResponseEntity<String> fmTest(Authentication authentication) {
        String username = authentication.getName();

        String html = renderHtml("test.ftlh", authentication, Map.of("user", username));

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }


    @GetMapping("/catalog")
    public ResponseEntity<String> catalog(Authentication authentication) {
        String username = authentication.getName();

        List<CatalogEntity> all = entityService.findAllEntities();


        String html = renderHtml("catalog.ftlh", authentication, Map.of(
                "user", username,
                "catalog", all));

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);
    }

}
