package com.structor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Flow.Subscriber;



public class AIConnector {

    
    //bodies of Http. Request is filled with example to prevent a null value
    private String requestBody = "{"
            + "\"model\": \"qwen3:8b\","
            + "\"prompt\": \"Hello world\","
            + "\"stream\": false,"
            + "\"temperature\": 0.7"
            + "}";
    private String responseBody = "No usege";
    //uri
    public static final String OllamaURI = "http://localhost:11434/api/generate";
    //generate a client to contact with models
    private HttpClient client = HttpClient.newHttpClient();

    public void setRequestBody(String requestBody){
        this.requestBody = requestBody;
    }

    public String getResponse(){
        String resp;
        int index1 = this.responseBody.indexOf("\"response\":", 0) + 12;
        try{
            resp = this.responseBody.substring(
                index1,
                this.responseBody.indexOf("\",\"thinking\":")
            );
        } catch (Exception e) {
            resp = this.responseBody.substring(
                index1,
                this.responseBody.indexOf("\",\"", index1)
            );
        }
        
        return resp.replace("\\n", "\n");
    }

    public void OllamaConnect(){
        //generate Http request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OllamaURI))
            .header("Content-Type", "application/json")
            .POST(BodyPublishers.ofString(this.requestBody))
            .build();

        //resive a HttpResponse
        HttpResponse<String> response = null;
        try {
            response = client.send(request, BodyHandlers.ofString());
            responseBody = response.body();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        // System.out.println(request + "\n" + requestBody + "\n");
        // System.out.println(response + "\n" + responseBody);
    }





}