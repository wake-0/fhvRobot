package app.robo.fhv.roboapp.utils;

import android.support.annotation.NonNull;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import app.robo.fhv.roboapp.domain.Score;

/**
 * Created by Kevin on 16.01.2016.
 */
public class XmlHelper {

    @NonNull
    public static Score[] parseHighScoreString(String highScore) {
        List<Score> scores = new ArrayList<>();

        try {
            String xml = highScore.replace("<?xml version=\"1.0\" encoding=\"utf-16\"?>", "");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("Score");

            // iterate the scores
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                String name = element.getAttribute("Name");
                String duration = element.getAttribute("Duration");
                scores.add(new Score(name, duration));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return scores.toArray(new Score[scores.size()]);
    }
}
