package org.example;

import dev.langchain4j.model.openai.OpenAiChatModel;
import org.testcontainers.containers.DockerModelRunnerContainer;

public class TestcontainersModelRunner {
    static String modelName = "ai/smollm2";

    public static void main(String[] args) {
        DockerModelRunnerContainer dmr = new DockerModelRunnerContainer("alpine/socat:1.7.4.3-r0")
                .withModel(modelName);
        dmr.start();

        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl(dmr.getOpenAIEndpoint() + "/v1")
                .modelName(modelName)
                .build();

        String answer = model.chat("Give me a fact about Formula 1.");
        System.out.println(answer);
    }
}
