package atlas.Inference;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class InferenceConfig {
    private static final Properties props = loadProperties();
    
    private static Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream input = InferenceConfig.class.getClassLoader()
                .getResourceAsStream("inference.properties")) {
            if (input != null) {
                p.load(input);
            }
        } catch (IOException e) {
            System.err.println("Error loading inference.properties: " + e.getMessage());
        }
        return p;
    }

    public static double getBeta() {
        return Double.parseDouble(props.getProperty("quality.beta", "3.0"));
    }

    public static double getAlpha() {
        return Double.parseDouble(props.getProperty("quality.alpha", "1.0"));
    }

    public static double getConfidenceThreshold() {
        return Double.parseDouble(props.getProperty("inference.confidence.threshold", "0.0"));
    }
}
