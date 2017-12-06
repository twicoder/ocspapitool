package com.asiainfo.xian;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    // java -jar <thisToolJarFile> method=PUT/POST url="<api address>" datafile="pathOfJsonFile"
    public static void main(String[] args) {
        String cmdFormat = "java -jar <thisToolJarFile> method=PUT/POST url=\"<api address>\" datafile=\"pathOfJsonFile\"";
        String method = null;
        String url = null;
        String dataFilePath = null;
        if (args.length != 3) {
            System.out.println(cmdFormat);
            return;
        } else {
            for (String param : args) {
                String[] keyValue = param.split("=");
                if (keyValue[0].trim().equalsIgnoreCase("method")) {
                    method = keyValue[1].trim();
                    if(method.equalsIgnoreCase("put") || method.equalsIgnoreCase("post")){

                    } else {
                        System.out.println("仅支持method=put和method=post");
                        System.out.println(cmdFormat);
                        return;
                    }
                } else if (keyValue[0].trim().equalsIgnoreCase("url")) {
                    url = keyValue[1].trim();
                } else if (keyValue[0].trim().equalsIgnoreCase("datafile")) {
                    dataFilePath = keyValue[1].trim();
                }
            }
        }
        if (method != null && url != null && dataFilePath != null) {
            String fileContent = readFileContent(dataFilePath);
            JsonReader jsonReader = Json.createReader(new StringReader(fileContent));
            JsonObject jsonContent = jsonReader.readObject();
            sendRequest(url,method,jsonContent);
        } else {
            System.out.println("Input Format Error:");
            System.out.println(cmdFormat);
            return;
        }
    }

    private static void sendRequest(String urlAddress, String method,JsonObject jsonContent) {
        try {
            URL url = new URL(urlAddress);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod(method.toUpperCase());
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type","application/json;charset=UTF-8");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.write(jsonContent.toString().getBytes("UTF-8"));
            out.flush();
            out.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            System.out.println(sb);
            reader.close();
            connection.disconnect();
        } catch (MalformedURLException exp) {
            exp.printStackTrace();
        } catch (UnsupportedEncodingException exp) {
            exp.printStackTrace();
        } catch (IOException exp) {
            exp.printStackTrace();
        }
    }

    private static String readFileContent(String dataFilePath) {
        String fileContent = "";
        File file = new File(dataFilePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                fileContent += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return fileContent;
    }
}
