package org.example;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.*;
import dev.langchain4j.model.chat.request.ChatRequestParameters;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Tools {

    static String modelName = "ai/llama3.2";

    public static void main(String[] args) {
        List<ChatMessage> messages = new ArrayList<>();

        messages.add(SystemMessage.from(
                "You can use tools to get additional information when needed."
        ));

        messages.add(UserMessage.from("What's the current time?"));

        ToolsHandler toolsHandler = new ToolsHandler();

        List<ToolSpecification> toolSpecifications = ToolSpecifications.toolSpecificationsFrom(toolsHandler);

        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl("http://localhost:12434/engines/v1")
                .modelName(modelName)
                .logRequests(true)
                .logResponses(true)
                .defaultRequestParameters(ChatRequestParameters.builder().toolSpecifications(toolSpecifications).build())
                .build();

        ChatResponse response = chatModel.chat(messages);
        AiMessage aiMessage = response.aiMessage();

        if (aiMessage.hasToolExecutionRequests()) {
            System.out.println(modelName + " is using tools to answer your question...");

            aiMessage.toolExecutionRequests().forEach(toolRequest -> {
                String toolName = toolRequest.name();
                String toolInput = toolRequest.arguments();

                System.out.println(modelName + "  is using tool: " + toolName);

                messages.add(aiMessage);
                try {
                    String result = toolsHandler.executeTool(toolName, toolInput);

                    messages.add(new ToolExecutionResultMessage(
                            toolRequest.id(),
                            toolName,
                            result
                    ));
                    messages.add(UserMessage.from(
                            "Based on the " + toolName + " tool result, please provide a helpful response. " +
                                    "The current time information has been provided, so please answer my question directly."
                    ));

                } catch (Exception e) {
                    System.out.println("Error executing tool: " + e.getMessage());
                    messages.add(new ToolExecutionResultMessage(
                            toolRequest.id(),
                            toolName,
                            "Error: " + e.getMessage()
                    ));
                }
            });

            ChatResponse followUpResponse = chatModel.chat(messages);
            AiMessage followUpMessage = followUpResponse.aiMessage();

            messages.add(followUpMessage);
            System.out.println(modelName + ": " + followUpMessage.text());
        } else {
            messages.add(aiMessage);
            System.out.println(modelName + ": " + aiMessage.text());
        }
    }

    static class ToolsHandler {

        @Tool("Get the current date and time")
        public String getCurrentDateTime() {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return "Current date and time: " + formatter.format(now);
        }

        @Tool("Read a file from the specified path")
        public String readFile(String filePath) {
            try {
                Path path = Paths.get(filePath);
                if (!Files.exists(path)) {
                    return "Error: File not found at " + filePath;
                }
                return "File content:\n" + Files.readString(path);
            } catch (IOException e) {
                return "Error reading file: " + e.getMessage();
            }
        }

        public String executeTool(String toolName, String toolInput) {
            switch (toolName) {
                case "getCurrentDateTime":
                    return getCurrentDateTime();
                case "readFile":
                    return readFile(toolInput);
                default:
                    return "Unknown tool: " + toolName;
            }
        }
    }
}