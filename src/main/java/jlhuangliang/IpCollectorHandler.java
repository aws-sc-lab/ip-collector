package jlhuangliang;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;


public class IpCollectorHandler implements RequestStreamHandler {

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {


    JSONParser parser = new JSONParser();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String sourceIp = "none";
    String serverIp = "none";
    try {
      JSONObject req = (JSONObject) parser.parse(reader);
      System.out.println(req.toJSONString());

      if (req.containsKey("headers")) {
        final JSONObject headers = (JSONObject) req.get("headers");
        System.out.println(headers.toJSONString());
        if (headers.containsKey("X-Forwarded-For")) {
          final String xForwardedFor = (String) headers.get("X-Forwarded-For");
          System.out.println(req.toJSONString());
          String[] ips = xForwardedFor.split(",");
          sourceIp = ips[0].trim();
          if (ips.length > 1) {
            serverIp = ips[1].trim();
          }
        }
      }
      System.out.println(req.toJSONString());
    } catch (ParseException e) {
      e.printStackTrace();
    }


    JSONObject responseJson = new JSONObject();

    JSONObject responseBody = new JSONObject();
    responseBody.put("sourceIp", sourceIp);
    responseBody.put("serverIp", serverIp);

    JSONObject headerJson = new JSONObject();
    headerJson.put("x-custom-header", "my custom header value");

    responseJson.put("statusCode", 200);
    responseJson.put("headers", headerJson);
    responseJson.put("body", responseBody.toString());


    OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
    writer.write(responseJson.toString());
    writer.close();
  }
}