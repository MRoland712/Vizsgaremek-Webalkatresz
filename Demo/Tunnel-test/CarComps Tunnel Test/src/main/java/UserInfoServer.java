import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UserInfoServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/userinfo", new UserInfoHandler());
        server.setExecutor(null); // default executor
        server.start();
        System.out.println("Server running on http://localhost:8080/userinfo");
    }
//Kis adatlopás, dont mind if i do :P
    static class UserInfoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                byte[] msg = "Method Not Allowed".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(405, msg.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(msg);
                }
                return;
            }

            Map<String, String> q = parseQuery(exchange.getRequestURI().getRawQuery());
            String userAgent = exchange.getRequestHeaders().getFirst("User-Agent");
            String ip = Optional.ofNullable(exchange.getRemoteAddress())
                    .map(a -> a.getAddress().getHostAddress())
                    .orElse("unknown");

            // Példa: csak azokat a mezőket tesszük bele, amit a kliens küldött (pl. name, email)
            String name = q.getOrDefault("name", "");
            String email = q.getOrDefault("email", "");
            String city = q.getOrDefault("city", "");

            String json = "{"
                    + "\"ip\":\"" + escape(ip) + "\","
                    + "\"userAgent\":\"" + escape(userAgent) + "\","
                    + "\"queryParams\":{"
                    + "\"name\":\"" + escape(name) + "\","
                    + "\"email\":\"" + escape(email) + "\","
                    + "\"city\":\"" + escape(city) + "\""
                    + "}"
                    + "}";

            byte[] response = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }

        private static Map<String, String> parseQuery(String rawQuery) {
            Map<String, String> map = new LinkedHashMap<>();
            if (rawQuery == null || rawQuery.isEmpty()) return map;
            String[] pairs = rawQuery.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf('=');
                if (idx > 0) {
                    String key = urlDecode(pair.substring(0, idx));
                    String val = urlDecode(pair.substring(idx + 1));
                    map.put(key, val);
                } else {
                    map.put(urlDecode(pair), "");
                }
            }
            return map;
        }

        private static String urlDecode(String s) {
            return URLDecoder.decode(s, StandardCharsets.UTF_8);
        }

        // Egyszerű escape JSON-hoz (csak a legfontosabb karakterekre)
        private static String escape(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
        }
    }
}