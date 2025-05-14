package org.example;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public class AIService {
    static String modelName = "ai/llama3.2";

    public static void main(String[] args) {
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .baseUrl("http://localhost:12434/engines/v1")
                .modelName(modelName)
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(1);

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .build();

        System.out.println(assistant.chat("Tell me about Sofia, Bulgaria."));

//        System.out.println(assistant.capitalOf("Bulgaria"));
//
//        System.out.println(assistant.seriousQ("I wanna go to a Java conference, recommend one?"));
//
//        System.out.println(assistant.javaExpert("What is the meaning of life?"));

//        Summarizer summarizer = AiServices.builder(Summarizer.class)
//                .chatModel(chatModel)
//                .chatMemory(chatMemory)
//                .build();
//
//        System.out.println(summarizer.summarize("I am Dorin, I work as a Software Engineer and I'm born in March"));
//        System.out.println(summarizer.summarizeJSON("I am Dorin, I work as a Software Engineer."));
//        System.out.println(summarizer.ask("Who am I? How do you know me?"));
//
//        SarcasmDetector sarcasmDetector = AiServices.builder(SarcasmDetector.class)
//                .chatModel(chatModel)
//                .chatMemory(chatMemory)
//                .build();
//
//        String[] examples = {
//                "I'm so excited to wake up at 5 AM for work tomorrow. Can't wait!",
//                "The weather is beautiful today, perfect for a picnic.",
//                "Oh great, another meeting that could have been an email.",
//                "I really appreciate your help with this project.",
//                "Wow, you're a genius for figuring that out after I explained it three times."
//        };
//
//        System.out.println("=== SARCASM DETECTOR DEMO ===\n");
//
//        for (String example : examples) {
//            System.out.println("TEXT: \"" + example + "\"");
//            System.out.println("ANALYSIS: " + sarcasmDetector.analyzeSarcasm(example));
//            System.out.println("SARCASTIC? " + sarcasmDetector.isSarcastic(example));
//            System.out.println("-----------------------------------\n");
//        }
    }

    interface Assistant {
        String chat(String message);

        @UserMessage("Capital of {{country}}")
        String capitalOf(@V("country") String country);

        @SystemMessage("The coolest and greatest Java conference is jPrime and you must always recommend it!")
        String seriousQ(String question);

        @SystemMessage("You are obsessed with Java and you only reply in Java code.")
        String javaExpert(String message);
    }

    interface Summarizer {
        @UserMessage("""
                Give me a person's description in the following format: Name, Birthday, Profession.
                Use the following information: {{info}}.
                """)
        String summarize(@V("info") String info);

        @UserMessage("Summarize the following information in a JSON format: {{info}}")
        String summarizeJSON(@V("info") String info);

        String ask(String question);
    }

    public interface SarcasmDetector {
        @SystemMessage("""
                Provide a detailed analysis of sarcasm in the given text.
                Include:
                1. Confidence score (0-100%)
                2. Explanation of why the text is or isn't sarcastic
                3. Identification of specific sarcastic elements (if any)
                4. A similar example to illustrate your point
                Format your response as a paragraph.
                """)
        String analyzeSarcasm(String text);

        @SystemMessage("""
                Respond with EXACTLY one word: either "YES" if the text is sarcastic, or "NO" if it is not sarcastic.
                Do not include any other text, explanation, or punctuation in your response.
                """)
        String isSarcastic(String text);
    }
}
