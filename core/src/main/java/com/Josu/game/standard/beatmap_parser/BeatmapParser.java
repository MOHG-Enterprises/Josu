package com.josu.game.standard.beatmap_parser;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.ArrayList;
import java.util.List;

public class BeatmapParser {
    public static class HitObject {
        public int x, y, time, type, hitSound;

        public HitObject(int x, int y, int time, int type, int hitSound) {
            this.x = x;
            this.y = y;
            this.time = time;
            this.type = type;
            this.hitSound = hitSound;
        }
    }

    public static class BeatmapData {
        public String audioFilename;
        public List<HitObject> hitObjects = new ArrayList<>();

        public BeatmapData(String audioFilename) {
            this.audioFilename = audioFilename;
        }
    }

    public static BeatmapData parse(String filePath) {
        FileHandle file = Gdx.files.internal(filePath);
        String[] lines = file.readString().split("\n");

        boolean inGeneralSection = false;
        boolean inHitObjectsSection = false;
        String audioFilename = "";

        BeatmapData beatmapData = new BeatmapData(audioFilename);

        for (String line : lines) {
            line = line.trim();
            if (line.equalsIgnoreCase("[General]")) {
                inGeneralSection = true;
                continue;
            } else if (line.equalsIgnoreCase("[HitObjects]")) {
                inHitObjectsSection = true;
                continue;
            } else if (line.startsWith("[") && !line.equalsIgnoreCase("[HitObjects]")) {
                inGeneralSection = false;
                inHitObjectsSection = false;
            }

            // Read audio filename from the [General] section
            if (inGeneralSection && line.startsWith("AudioFilename")) {
                audioFilename = line.split(":")[1].trim();
                beatmapData.audioFilename = audioFilename;
            }

            // Read hit objects
            if (inHitObjectsSection && !line.isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    try {
                        int x = Integer.parseInt(parts[0].trim());
                        int y = Integer.parseInt(parts[1].trim());
                        int time = Integer.parseInt(parts[2].trim());
                        int type = Integer.parseInt(parts[3].trim());
                        int hitSound = Integer.parseInt(parts[4].trim());

                        beatmapData.hitObjects.add(new HitObject(x, y, time, type, hitSound));
                    } catch (NumberFormatException e) {
                        Gdx.app.log("BeatmapParser", "Error parsing hit object: " + line, e);
                    }
                }
            }
        }
        return beatmapData;
    }
}

