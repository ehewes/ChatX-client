package com.chatx.config;

import java.io.FileNotFoundException;
import java.util.Scanner;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import com.github.cliftonlabs.json_simple.JsonException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GetClientDetails {
        public static String checkExistingClient() {
            String username = null;
            try (FileReader reader = new FileReader("client-details.json")) {
                JsonObject jsonObject = (JsonObject) Jsoner.deserialize(reader);

                if (jsonObject.containsKey("username")) {
                    username = (String) jsonObject.get("username");
                    System.out.println("Welcome back, " + username + "!");
                } else {
                    username = promptForUsername();
                }
            } catch (FileNotFoundException e) {
                username = promptForUsername();
            } catch (IOException | JsonException e) {
                throw new RuntimeException(e);
            }
            return username;
        }

        public static String promptForUsername() {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();

            JsonObject jsonObject = new JsonObject();
            jsonObject.put("username", username);

            try (FileWriter file = new FileWriter("client-details.json")) {
                file.write(jsonObject.toJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return username;
        }
}