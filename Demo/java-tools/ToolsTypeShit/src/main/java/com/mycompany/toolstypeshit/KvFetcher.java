/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.toolstypeshit;

/**
 *
 * @author ddori
 */
import java.io.IOException;
import java.net.http.*;
import java.net.URI;
import com.mycompany.toolstypeshit.base64Converters;

public class KvFetcher {

    public static String getDataFromKV(String keyName) {
        try {
            String B64AccountId = System.getenv("CF_ACCOUNT_ID");
            String B64NamespaceId = System.getenv("CF_NAMESPACE_ID");
            String B64ApiToken = System.getenv("CF_API_TOKEN");

            if (B64AccountId == null || B64NamespaceId == null || B64ApiToken == null) {
                System.err.println("Nincs beállítva valamelyik env :c");
            }

            String accountId = base64Converters.base64Converter(B64AccountId);
            String namespaceId = base64Converters.base64Converter(B64AccountId);
            String apiToken = base64Converters.base64Converter(B64AccountId);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.cloudflare.com/client/v4/accounts/"
                            + accountId + "/storage/kv/namespaces/"
                            + namespaceId + "/values/" + keyName))
                    .header("Authorization", "Bearer " + apiToken)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RuntimeException("Cloudflare KV error: " + response.statusCode() + " - " + response.body());
            }
        } catch (IOException | InterruptedException e ) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to fetch KV value", e);

        }
    }

}
