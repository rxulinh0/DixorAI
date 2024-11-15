package com.ai.dixorai;

import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.ArrayList;

public class SpeechRecognitionListener implements RecognitionListener  {
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.d("SpeechRecognition", "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d("SpeechRecognition", "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        Log.d("SpeechRecognition", "onRmsChanged: " + rmsdB);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.d("SpeechRecognition", "onBufferReceived");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d("SpeechRecognition", "onEndOfSpeech");
    }

    @Override
    public void onError(int error) {
        Log.d("SpeechRecognition", "onError: " + error);
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null && !matches.isEmpty()) {
            String text = matches.get(0);
            Log.d("SpeechRecognition", "onResults: " + text);
        }
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.d("SpeechRecognition", "onPartialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.d("SpeechRecognition", "onEvent: " + eventType);
    }
}
