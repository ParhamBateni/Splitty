package server.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/rates")
public class CurrencyExchangeController {

    @GetMapping("/{date}")
    public ResponseEntity<Map<String, Double>> getRates(@PathVariable("date") String date) {
        Response response;
        if (date.equals(LocalDate.now().toString())) {
            response = ClientBuilder.newClient(new ClientConfig())
                    .target("https://openexchangerates.org")
                    .path("api/latest.json")
                    .queryParam("app_id", "46c2d5697db14c16a7fdafed2a5bff1c")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON).get();
        } else {
            response = ClientBuilder.newClient(new ClientConfig())
                    .target("https://openexchangerates.org")
                    .path("api/historical/{date}.json")
                    .resolveTemplate("date", date)
                    .queryParam("app_id", "46c2d5697db14c16a7fdafed2a5bff1c")
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON).get();
        }
        if (response.getStatus() != 200) {
            return ResponseEntity.status(response.getStatus()).build();
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Double> rates = new HashMap<>();
            try {
                Map<String, Object> obj = objectMapper.readValue(response.readEntity(String.class),
                        Map.class);
                Map<String, Object> ratesObj = ((Map<String, Object>) obj.get("rates"));
                for (String key : ratesObj.keySet()) {
                    rates.put(key, Double.parseDouble(ratesObj.get(key).toString()));
                }
                return ResponseEntity.ok(rates);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
