package upb.ida.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class checkServerController {

    @GetMapping("/check")
    public String greeting() {
        return "IDA Uniform Data Server is up and running";
    }
}