package com.example.redis;

import redis.clients.jedis.Jedis;

import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static Jedis jedis;
    private static Map<String, String> keyValueStore = new ConcurrentHashMap<>();
    

    public static void main(String[] args) {
        jedis = new Jedis("localhost", 6379);
        String channel = "your-channel";

        // Run the subscription in a separate CompletableFuture
        CompletableFuture<Void> subscriptionFuture = CompletableFuture.runAsync(() -> {
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    processRedisCommand(message);
                }
            }, channel);
        });

        // Perform other tasks concurrently if needed

        // Wait for the subscription to complete
        subscriptionFuture.join();

        jedis.close();
    }

    private static void processRedisCommand(String command) {
        
    	String[] arrayContent = command.split("\r\n");
        
        if (arrayContent.length > 1)	processRedisArray(arrayContent);
            
        
    }

    private static void processRedisArray(String[] arrayContent) {
        String command = arrayContent[2].toUpperCase();

        // Find the position of the command in the array
        
        if (command != null) {
            switch (command) {
                case "SET":
                    if (arrayContent.length == 7) {
                        String key = arrayContent[4];
                        String value = arrayContent[6]; 
                        keyValueStore.put(key, value);
                        System.out.println("SET command executed successfully");
                    } else {
                        System.out.println("Error: SET command format is incorrect");
                    }
                    break;
                case "GET":
                    if (arrayContent.length == 5) {
                        String key = arrayContent[4];
                        String value = keyValueStore.get(key);
                        System.out.println("GET result: " + value);
                    } else {
                        System.out.println("Error: GET command format is incorrect");
                    }
                    break;
                default:
                    System.out.println("Unknown Redis command: " + command);
            }
        } else {
            System.out.println("Error: No valid command found in the array");
        }
    }

}
// /r/n
