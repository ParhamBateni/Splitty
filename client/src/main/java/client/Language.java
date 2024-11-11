package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class Language {

    private Map<String, String> dict;
    private String currentLanguage;

    public Language(String language) {
        try {
            if (language != null) {
                if (language.equals("EN")
                        || language.equals("DE") || language.equals("NL")) {
                    currentLanguage = language;
                    ObjectMapper objectMapper = new ObjectMapper();
                    URL res = getClass()
                            .getClassLoader()
                            .getResource("client/languages/" + language + ".json");
                    File file = Paths.get(res.toURI()).toFile();

                    dict = objectMapper.readValue(file, Map.class);
                }
            } else {
                throw new IllegalArgumentException("Incompatible language " +
                        "entered in config file! Select from EN, NL, or DE");
            }
        } catch (Exception e) {
            System.out.println(e);
            currentLanguage = "EN";
        }
    }

    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return Map.class.getClassLoader().getResource(path);
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public String getText(String text) {
        if (text == null) {
            return "";
        }
        return dict.getOrDefault(text, text);
    }

    public ImageView getImageView(String image) {
        switch (image) {
            case "EN":
                Image englishImage = new Image("client/images/englishIcon.png");
                ImageView engishImageView = new ImageView(englishImage);
                engishImageView.setFitHeight(20);
                engishImageView.setPreserveRatio(true);
                return engishImageView;
            case "DE":
                Image germanImage = new Image("client/images/germanIcon.png");
                ImageView germanImageView = new ImageView(germanImage);
                germanImageView.setFitHeight(20);
                germanImageView.setPreserveRatio(true);
                return germanImageView;
            case "NL":
                Image dutchImage = new Image("client/images/dutchIcon.png");
                ImageView dutchImageView = new ImageView(dutchImage);
                dutchImageView.setFitHeight(20);
                dutchImageView.setPreserveRatio(true);
                return dutchImageView;
            default:
                throw new RuntimeException();
        }
    }
}
