package com.ai.dixorai;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;

public class GeneratePrompt {
    private String prompt;
    private Executor executor;

    public GeneratePrompt(String prompt, Executor executor) {
        this.prompt = prompt;
        this.executor = executor;
    }
    public String genPrompt(String prompt){
        final String[] ai_generated_response = new String[1];
        String API_KEY = BuildConfig.API_KEY;
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                ai_generated_response[0] = removeFirstLine(result.getText());
                System.out.println(ai_generated_response[0]);
                //viewModel.addWord(basic_user_data.getAllWords().get(0));
                //startTypewriterEffect(word_def_tv,resultText);
                //wordRvAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        return ai_generated_response[0];
    }

    public String genQuestionPrompt(String prompt){
        final String[] ai_generated_response = new String[1];
        String API_KEY = BuildConfig.API_KEY;
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ API_KEY);
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);
        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                ai_generated_response[0] = removeFirstLine(result.getText());
                System.out.println(ai_generated_response[0]);
                basic_user_data.addCounterTriviaQuestionsGenerated();
                //viewModel.addWord(basic_user_data.getAllWords().get(0));
                //startTypewriterEffect(word_def_tv,resultText);
                //wordRvAdapter.notifyItemChanged(position);
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
        return ai_generated_response[0];
    }
    private static String removeFirstLine(String text) {
        if(!(text.charAt(0)=='#')){
            return text;
        }
        // Find the index of the first newline character
        int indexOfFirstNewline = text.indexOf('\n');

        // If there's no newline, return an empty string
        if (indexOfFirstNewline == -1) {
            return "";
        }

        // Return the substring from the character after the first newline to the end
        return text.substring(indexOfFirstNewline + 1);
    }
}
