package com.Josu.game;

public class Beatmap {
    public String song;
    public int bpm;
    public float offset;
    public CircleData[] notes;

    static class CircleData {
        public float time;
        public float x;
        public float y;
        public float size;
    }
}
