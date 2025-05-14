package org.example;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class MemoryAndConversationalChain {

    static String modelName = "ai/llama3.2";

    public static void main(String[] args) {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl("http://localhost:12434/engines/v1")
                .modelName(modelName)
//                .logRequests(true)
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(1);

        ConversationalChain chain = ConversationalChain.builder()
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .build();

        System.out.println("Answer: " + chain.execute("My name is Dorin."));
        System.out.println("Answer: " + chain.execute("What is my name?"));
    }
}