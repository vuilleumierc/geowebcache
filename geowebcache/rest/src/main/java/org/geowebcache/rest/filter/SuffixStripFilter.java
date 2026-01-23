package org.geowebcache.rest.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SuffixStripFilter implements Filter {

    private static final Pattern EXTENSION_PATTERN = Pattern.compile("^(.*?)\\.(json|xml)$");

    public static final String FORMAT_ATTRIBUTE = "gwc.formatExtension";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpRequest) {
            String requestURI = httpRequest.getRequestURI();
            Matcher matcher = EXTENSION_PATTERN.matcher(requestURI);

            if (matcher.matches()) {
                String pathWithoutExtension = matcher.group(1);
                String extension = matcher.group(2);

                // Wrap the request to return modified paths
                HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpRequest) {
                    @Override
                    public String getRequestURI() {
                        return pathWithoutExtension;
                    }

                    @Override
                    public StringBuffer getRequestURL() {
                        StringBuffer url =
                                new StringBuffer(super.getRequestURL().toString());
                        int extIndex = url.lastIndexOf("." + extension);
                        if (extIndex > 0) {
                            url.delete(extIndex, url.length());
                        }
                        return url;
                    }

                    @Override
                    public String getServletPath() {
                        String servletPath = super.getServletPath();
                        return servletPath.replaceFirst("\\." + extension + "$", "");
                    }
                };

                // Store extension for content negotiation
                wrapper.setAttribute(FORMAT_ATTRIBUTE, extension);

                chain.doFilter(wrapper, response);
                return;
            }
        }

        // No extension found, pass through unchanged
        chain.doFilter(request, response);
    }
}
