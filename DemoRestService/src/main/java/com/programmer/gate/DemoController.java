package com.programmer.gate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class DemoController {
	
    

    
    

    
    
    @RequestMapping("/euclid")
    public int sum(@RequestParam(value="x1") double first, @RequestParam(value="y1")double second, @RequestParam(value="x2") double third, @RequestParam(value="y2") double forth) {
    	
    	return (int)Math.sqrt(((first-third)*(first-third))+((second-forth)*(second-forth)));
    }
    
}
