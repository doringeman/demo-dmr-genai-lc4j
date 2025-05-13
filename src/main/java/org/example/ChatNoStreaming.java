package org.example;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.util.Scanner;

public class ChatNoStreaming {
    static String modelName = "ai/llama3.2";

    public static void main(String[] args) {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("http://localhost:12434/engines/v1")
                .modelName(modelName)
                .build();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Chat with " + modelName + " (type /bye to exit):");

        while (true) {
            System.out.print("> ");
            String userInput = scanner.nextLine();

            if ("/bye".equalsIgnoreCase(userInput)) {
                break;
            }

            ChatResponse response = model.chat(UserMessage.from(userInput));

            System.out.println(modelName + ": " + response.aiMessage().text());
        }

        scanner.close();
        System.out.println("Chat ended. Goodbye!");
    }
}