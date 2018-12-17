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
import java.net.InetAddress;


public class IpCollectorHandler implements RequestStreamHandler {

  @Override
  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {


    JSONParser parser = new JSONParser();
    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
    String sourceIp = "none";
    try {
      JSONObject event = (JSONObject) parser.parse(reader);

      if (event.containsKey("requestContext")) {
        final JSONObject requestContext = (JSONObject) event.get("requestContext");

        if (requestContext.containsKey("identity")) {
          final JSONObject identity = (JSONObject) requestContext.get("identity");
          sourceIp = (String) identity.get("sourceIp");
        }
      }
      System.out.println(event.toJSONString());

    } catch (ParseException e) {
      e.printStackTrace();
    }

    InetAddress inetAddress = InetAddress.getLocalHost();
    System.out.println("IP Address: "+inetAddress.getHostAddress());


    JSONObject responseJson = new JSONObject();

    JSONObject responseBody = new JSONObject();
    responseBody.put("sourceIp", sourceIp);
    responseBody.put("serverIp", inetAddress.getHostAddress());

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