package app.robo.fhv.roboapp.domain;

import javax.xml.datatype.Duration;

/**
 * Created by Kevin on 15.01.2016.
 */
public class Score {

    private final String name;
    private final String duration;

    public Score(String name, String duration) {
        this.name = name;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public  String getDuration() {
        return duration;
    }
}
