package client.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CurrencyExchangeService {
    private String ratesPath;
    private Map<String, Map<String, Double>> savedRates;

    @Inject
    public CurrencyExchangeService() {
        ratesPath = (CurrencyExchangeService.class.getClassLoader()
                .getResource("client").getFile() + "/rates/").replace("main", "")
                .replace("build", "src/main").replace("%20", " ").replace("java", "")
                .replaceAll("//", "/").replace("classes", "resources");
        savedRates = new HashMap<>();

    }


    public double getExchangeRate(Currency from, Currency to,
                                  String date, ServerUtils server) {
        if (from.equals(to)) {
            return 1.0;
        }
        String fileName = String.format("rates_%s.json", date);
        String fileAddress = ratesPath + fileName;
        Map<String, Double> exchangeRates;
        boolean ratesUpdated = false;
        // Checking if the rate is requested within the last hour use the local cached rate
        // if not send a request to the server to get the live rates.
        if (savedRates.containsKey(date)) {
            exchangeRates = savedRates.get(date);
        } else {
            try {
                exchangeRates = loadRatesFromLocalCache(fileAddress);
            } catch (IOException e) {
                System.out.println("Currency rates " + fileAddress + " not found!\n" +
                        "Fetching from server...");
                exchangeRates = server.getExchangeRates(date);
                if (exchangeRates == null) {
                    System.out.println("Can not get exchange rates from server!");
                    return -1.0;
                }
                ratesUpdated = true;
            }
        }
        savedRates.put(date, exchangeRates);
        if (ratesUpdated) {
            try {
                String newFileName = String.format("rates_%s.json", date);
                String newFileAddress = ratesPath + newFileName;
                saveRatesOnLocalCache(exchangeRates,
                        newFileAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(exchangeRates.get(to.toString()));
        System.out.println(exchangeRates.get(from.toString()));
        return exchangeRates.get(to.toString()) / exchangeRates.get(from.toString());
    }

    private Map<String, Double> loadRatesFromLocalCache(String fileAddress)
            throws IOException {
        Map<String, Double> exchangeRates = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(fileAddress);
        JsonNode rootNode = objectMapper.readTree(jsonFile);
        try {
            for (Iterator<String> it = rootNode.fieldNames(); it.hasNext(); ) {
                String currency = it.next();
                JsonNode node = rootNode.get(currency);
                exchangeRates.put(currency, node.asDouble());
            }
            return exchangeRates;
        } catch (Exception e) {
            throw new IOException("Could not locate " + fileAddress +
                    " to read the exchange rates or there is" +
                    " a problem with the json file!");
        }
    }

    public void saveRatesOnLocalCache(Map<String, Double> rates, String fileAddress)
            throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File jsonFile = new File(fileAddress);
            objectMapper.writeValue(jsonFile, rates);
//            SplittyConfig.setLastRequestedRateTime(LocalDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Could not locate " + fileAddress +
                    " to store the exchange rates or there is" +
                    " problem with storing the rates!");
        }
    }
}
