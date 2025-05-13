package org.example;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ChatStreaming {
    static String modelName = "ai/llama3.2";

    public static void main(String[] args) {
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
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

            CountDownLatch done = new CountDownLatch(1);
            model.chat(userInput, new StreamingChatResponseHandler() {
                @Override
                public void onPartialResponse(String partialResponse) {
                    System.out.print(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    System.out.println();
                    done.countDown();
                }

                @Override
                public void onError(Throwable error) {
                    System.err.println("\nError: " + error.getMessage());
                    done.countDown();
                }
            });

            try {
                done.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        scanner.close();
        System.out.println("Chat ended. Goodbye!");
    }
}
