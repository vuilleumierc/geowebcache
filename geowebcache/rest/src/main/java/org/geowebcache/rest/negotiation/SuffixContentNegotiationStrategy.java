package org.geowebcache.rest.negotiation;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

public class SuffixContentNegotiationStrategy implements ContentNegotiationStrategy {

    public static final String FORMAT_ATTRIBUTE = "gwc.formatExtension";

    @Override
    public List<MediaType> resolveMediaTypes(NativeWebRequest request) {
        HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
        if (servletRequest != null) {
            // Check if filter stored the extension
            String extension = (String) servletRequest.getAttribute(FORMAT_ATTRIBUTE);

            if (extension != null) {
                if ("json".equals(extension)) {
                    return Collections.singletonList(MediaType.APPLICATION_JSON);
                } else if ("xml".equals(extension)) {
                    return Collections.singletonList(MediaType.APPLICATION_XML);
                }
            }
        }
        return Collections.emptyList();
    }
}
