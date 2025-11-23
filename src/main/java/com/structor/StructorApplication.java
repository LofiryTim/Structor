package com.structor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class StructorApplication {
      
      public static String model = "qwen3:8b";
      public static String prompt = "Wold hello fuck me";
      public static String think = "false";

      public static String setReq(String model, String prompt, String think){
            String req = "{"
            + "\"model\": \"" + model + "\","
            + "\"prompt\": \"" + prompt + "\","
            + "\"stream\": false,"
            + "\"think\": " + think + ","
            + "\"temperature\": 0.7"
            + "}";
            return req;
      }
	public static void main(String[] args) {


            AIConnector connector = new AIConnector();
            connector.setRequestBody(setReq(model, prompt, think));
		connector.OllamaConnect();
            System.out.println(connector.getResponse());
		

		
	}

}
