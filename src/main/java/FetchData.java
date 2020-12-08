
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;



public class FetchData{

//    private static HttpURLConnection connection;
    String webAddress;
    private String result;



    public FetchData(String webAddress) {
        this.webAddress = webAddress;
    }



    public void FetchData(){
 //        this.result = result;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webAddress))
                .build();
        CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            result = response.thenApply(HttpResponse::body)
                    .join();
        }

    public String getResult() {
        return this.result;
    }



}




