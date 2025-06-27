package com.example.mobile.model;

import java.util.List;

public class TranscriptionResponse {
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        private List<Alternative> alternatives;

        public List<Alternative> getAlternatives() {
            return alternatives;
        }
    }

    public static class Alternative {
        private String transcript;

        public String getTranscript() {
            return transcript;
        }
    }
}
