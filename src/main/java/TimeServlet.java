import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import org.thymeleaf.context.Context;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/time/*")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();

       FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:/Users/Tanya/project1/mjdule9Thymeleaf/src/main/resources/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String timezone = req.getParameter("timezone");
        if (timezone == null) {
            timezone = timezoneCookie(req);
        } else {
            timezone = timezone.replace(' ', '+');
            resp.addCookie(new Cookie("lastTimezone", timezone));
        }

        String currentTime = ZonedDateTime
                .now(ZoneId.of(timezone))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ")) + timezone;

        Context simpleContext = new Context(req.getLocale(), Map.of("time", currentTime));

        engine.process("time", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    private static String timezoneCookie(HttpServletRequest req) {
        String cookie = req.getHeader("Cookie");

        Map<String, String> result = new HashMap<>();

        if (cookie != null) {
            String[] splitCookies = cookie.split(";");
            for (String val : splitCookies) {
                String[] keyValue = val.split("=");
                result.put(keyValue[0], keyValue[1]);
            }
        }
        return result.getOrDefault("lastTimezone", "UTC");
    }
}
