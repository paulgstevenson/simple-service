package com.example.pi;

public class Pi {

    public static class Calculate {

        private final int nrOfMessages;
        private final int nrOfElements;

        public Calculate(int nrOfMessages, int nrOfElements){
            this.nrOfMessages = nrOfMessages;
            this.nrOfElements = nrOfElements;
        }

        public int getNrOfElements() {
            return nrOfElements;
        }

        public int getNrOfMessages() {
            return nrOfMessages;
        }
    }

    static class Work {
        private final int start;
        private final int nrOfElements;

        public Work(int start, int nrOfElements) {
            this.start = start;
            this.nrOfElements = nrOfElements;
        }

        public int getStart() {
            return start;
        }

        public int getNrOfElements() {
            return nrOfElements;
        }
    }

    static class Result {
        private final double value;

        public Result(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    public static class PiApproximation {
        private final double pi;
        private final long duration;

        public PiApproximation(double pi, long duration) {
            this.pi = pi;
            this.duration = duration;
        }

        public double getPi() {
            return pi;
        }

        public long getDuration() {
            return duration;
        }
    }


}
