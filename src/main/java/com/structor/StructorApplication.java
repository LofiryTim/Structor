package com.structor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;
import java.io.*;
import java.nio.charset.StandardCharsets;


@SpringBootApplication
public class StructorApplication {
      
      public static String model = "qwen3-vl:235b-cloud";
      public static boolean isImge = true;
      public static String prompt = "Далее после двоеточия будет переписка с нейросетью, сообщения разделены на ответы и запросы. Оценивай переписку и отвечай на последний запрос пользователя так, будто это с тобой он переписывался, возможно будут приложены картики, имей ввиду, если в после ввода(в последнем) в конце ничего нету то объясняй картинку:" ;
      public static String think = "true";
      public static String tempImagePATH = "/Users/lofiry/Documents/Structor/StructorApp/tmp/clipboard.png";
      public static String imageBase64;
      public static String temperature = "1";
      public static String filePATH = "/Users/lofiry/Documents/Structor/StructorApp/tmp/response.md";
      public static Path imagePath = null;
      

      public static void saveClipboardImageToTempFile(){
            String command = "pngpaste " + tempImagePATH;
            try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            } catch (Exception e) {
            System.err.println("Ошибка сохранения из буфера: " + e.getMessage());
            }
      }

      public static String setReq(String model_, String prompt_, String think_, String tempImagePath_){
            String imageBase64_ = "";

            saveClipboardImageToTempFile();
            //imagePath = Paths.get(tempImagePath_);

            
            String req;
            
            //if(Files.exists())
            try { 
                  //getting Path of image
                  imagePath = Paths.get(tempImagePATH);
                  
                  //making encodered string from image to send in json
                  
                  imageBase64_ = Base64.getEncoder().encodeToString(Files.readAllBytes(imagePath));
            } catch (IOException e) {
                  System.err.println("Ошибка: Временный файл пустой или не создан.");
                  e.printStackTrace();
                  isImge = false;
            }

            //checking and choose the request type
            if(!isImge)
            {
                  System.out.println("Ошибка: Изображение не найдено в буфере обмена. Сообщение обрабатывается без изображения");
                  req = "{\n" + //
                        "  \"model\": \""+ model_ +"\",\n" + //
                        "  \"prompt\": \""+ prompt_ +"\",\n" + //
                        "  \"stream\": false,\n" + //
                        "  \"temperature\": " + temperature + //
                       // ",  \"images\": [\""+ imageBase64_ +"\"]\n" + //
                        "}";
                  System.out.println(req);
                  return req;
            }
            else{
                   req = "{\n" + //
                        "  \"model\": \""+ model_ +"\",\n" + //
                        "  \"prompt\": \""+ prompt_ +"\",\n" + //
                        "  \"stream\": false,\n" + //
                        "  \"temperature\": " + temperature +//
                        ",\n  \"images\": [\""+ imageBase64_ +"\"]" + //
                        "}";
                  System.out.println(req);
                  return req;
            }
      }

      //writing the pesponse to the file to work with
      public static void writeResp(String resp, String respPATH_) {
            try{ 
                  if(!Files.exists(Paths.get(respPATH_))){
                        Process process = Runtime.getRuntime().exec("touch " + respPATH_);
                        process.waitFor();
                  }
                        FileWriter writer = new FileWriter(respPATH_, true);
                        writer.write("\n\n**Ответ от " + model + ":**\n" + resp + "\n\n\n**ВВОД:**");
                        writer.flush();
                        writer.close();

            } catch (Exception e){
                  System.out.println(e.getMessage() + "\n" + e.getCause());
            }
      }
      //reading the file text to work with
      public static String readPrompt(String promtPATH_){
            try{
                  BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(promtPATH_), "UTF-8"));
                  String prompt_ = "";
                  String line;
                  while ((line = reader.readLine()) != null) {
                        prompt_ = prompt_ + line;
                  } 
                  return prompt_;
            } catch (Exception e) {
                  System.out.println(e.getMessage() + "\n" + e.getCause());
                  return "";
            }
            
      }
	public static void main(String[] args) {

            
            //main action
            prompt = (prompt + readPrompt(filePATH)).replace("\n", "\\n");
            AIConnector connector = new AIConnector();
            connector.setRequestBody(setReq(model, prompt, think, tempImagePATH));
		connector.OllamaConnect();
            String resp = connector.getResponse();
            writeResp(resp, filePATH);
		
            try {
                  Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                   e.printStackTrace();
            }

		
	}

}
