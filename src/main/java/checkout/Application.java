package checkout;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.adyen.model.checkout.*;
import com.google.gson.Gson;

import view.RenderUtil;

import static spark.Spark.get;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;


public class Application {

    private static final String CONFIG_FILE = "config.properties";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        port(8080);
        staticFiles.location("/static");
        Properties prop = readConfigFile();

        System.out.println(prop.getProperty("merchantAccount"));
        String clientKey = prop.getProperty("clientKey");
        CheckoutService checkoutService = new CheckoutService(prop);

        // Routes

        get("/", (req, res) -> {
            Map<String, Object> context = new HashMap<>();
            return RenderUtil.render(context, "templates/home.html");
        });

        get("/cart/:integration", (req, res) -> {
            String integrationType = req.params(":integration");

            Map<String, Object> context = new HashMap<>();
            context.put("integrationType", "/checkout/" + integrationType);

            return RenderUtil.render(context, "templates/cart.html");
        });

        get("/checkout/:integration", (req, res) -> {
            String integrationType = req.params(":integration");

            Map<String, Object> context = new HashMap<>();
            context.put("clientKey", clientKey);
            context.put("integrationType", integrationType);

            return RenderUtil.render(context, "templates/component.html");
        });

        path("/result", () -> {
            get("/success", (req, res) -> {
                Map<String, Object> context = new HashMap<>();
                return RenderUtil.render(context, "templates/checkout-success.html");
            });

            get("/failed", (req, res) -> {
                Map<String, Object> context = new HashMap<>();
                return RenderUtil.render(context, "templates/checkout-failed.html");
            });

            get("/pending", (req, res) -> {
                Map<String, Object> context = new HashMap<>();
                return RenderUtil.render(context, "templates/checkout-success.html");
            });

            get("/error", (req, res) -> {
                Map<String, Object> context = new HashMap<>();
                return RenderUtil.render(context, "templates/checkout-failed.html");
            });
        });

        get("/redirect", (req, res) -> {
            Map<String, Object> context = new HashMap<>();
            context.put("clientKey", clientKey);
            return RenderUtil.render(context, "templates/redirect.html");
        });

        post("/api/sessions", (req, res) -> {
            CreateCheckoutSessionResponse response = checkoutService.checkoutsessions(req);
            return gson.toJson(response);
        });

        System.out.println("\n----------------------------------------------------------\n\t" +
            "Application is running! Access URLs:\n\t" +
            "Local: \t\thttp://localhost:8080\n\t" +
            "\n----------------------------------------------------------");
    }

    private static Properties readConfigFile() {

        Properties prop = new Properties();

        // Reading environment variables
//        prop.setProperty("merchantAccount", System.getenv("ADYEN_MERCHANT_ACCOUNT"));
//        prop.setProperty("apiKey", System.getenv("ADYEN_API_KEY"));
//        prop.setProperty("clientKey",System.getenv("ADYEN_CLIENT_KEY"));

        prop.setProperty("merchantAccount", "AaronZhu");
        prop.setProperty("apiKey", "AQEyhmfxKo7PbxVCw0m/n3Q5qf3VaY9UCJ1+XWZe9W27jmlZinbf1STXpfKfm86QTTaJKzEQwV1bDb7kfNy1WIxIIkxgBw==-gSgbrAEAnRDj2TtWMQzkdFebgfqPCEAYogicxk6cyG8=-@dp3VyAq_$cqU2^7");
        prop.setProperty("clientKey","test_GBI6RMQCHZCJHA5XQM2KSRQLYQFEMRZC");

        // Overriding with local properties

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(CONFIG_FILE))) {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return prop;
    }
}
