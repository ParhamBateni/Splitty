package server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * SomeController class
 */
@Controller
@RequestMapping("/")
public class SomeController {

    private final SplittyConfig splittyConfig;

    @Autowired
    public SomeController(SplittyConfig splittyConfig) {
        this.splittyConfig = splittyConfig;
    }

    @GetMapping("/")
    @ResponseBody
    public String index() throws IOException {
        // return splittyConfig.getServerUrl();  --> how to access server url
        return "Hello World";
    }
}
