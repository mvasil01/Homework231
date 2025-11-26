import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AutocompleteServer {

    private static AutocompleteEngine engine;

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: java AutocompleteServer <dictionary_file> <training_file>");
            return;
        }

        String dictFile = args[0];
        String trainFile = args[1];

        // 1. Initialize engine (loads trie)
        engine = new AutocompleteEngine(dictFile, trainFile);

        // 2. Start HTTP server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        // 3. Register endpoints
        server.createContext("/topk", new TopKHandler());
        server.createContext("/avg", new AvgHandler());
        server.createContext("/next", new NextHandler());
        server.createContext("/search", new SearchHandler());
        server.createContext("/", new RootHandler()); // serves a simple welcome

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on http://localhost:8000");
    }

    // Utility: parse query parameters ?a=1&b=2
    private static Map<String, String> queryToMap(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String param : query.split("&")) {
            String[] parts = param.split("=", 2);
            if (parts.length == 2) {
                map.put(parts[0], java.net.URLDecoder.decode(parts[1], StandardCharsets.UTF_8));
            }
        }
        return map;
    }

    private static void sendText(HttpExchange exchange, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // allow browser
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    // === Handlers ===

    static class RootHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String msg = "Autocomplete server is running.\n" +
                         "Endpoints:\n" +
                         "  /topk?prefix=ap&k=5\n" +
                         "  /avg?prefix=ap\n" +
                         "  /next?prefix=ap\n" +
                         "  /search?word=apple\n";
            sendText(exchange, msg);
        }
    }

    static class TopKHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Map<String, String> params = queryToMap(uri.getQuery());
            String prefix = params.getOrDefault("prefix", "");
            int k;
            try {
                k = Integer.parseInt(params.getOrDefault("k", "5"));
            } catch (NumberFormatException e) {
                k = 5;
            }

            WordFrequency[] arr = engine.topK(prefix, k);

            // Build simple JSON: [{"word":"apple","importance":4}, ...]
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) sb.append(",");
                sb.append("{\"word\":\"")
                  .append(arr[i].word)
                  .append("\",\"importance\":")
                  .append(arr[i].importance)
                  .append("}");
            }
            sb.append("]");

            sendText(exchange, sb.toString());
        }
    }

    static class AvgHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Map<String, String> params = queryToMap(uri.getQuery());
            String prefix = params.getOrDefault("prefix", "");
            double avg = engine.avgFreq(prefix);
            sendText(exchange, Double.toString(avg));
        }
    }

    static class NextHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Map<String, String> params = queryToMap(uri.getQuery());
            String prefix = params.getOrDefault("prefix", "");
            char c = engine.nextLetter(prefix);
            String resp = (c == '\0') ? "" : String.valueOf(c);
            sendText(exchange, resp);
        }
    }

    static class SearchHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            URI uri = exchange.getRequestURI();
            Map<String, String> params = queryToMap(uri.getQuery());
            String word = params.getOrDefault("word", "");
            boolean found = engine.search(word);
            sendText(exchange, Boolean.toString(found));
        }
    }
}