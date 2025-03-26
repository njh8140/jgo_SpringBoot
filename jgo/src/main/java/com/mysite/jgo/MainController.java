package com.mysite.jgo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class MainController {

	@GetMapping("/jgo")
	@ResponseBody
	public String index() {
		System.out.println("index");
		return "jgo test";
	}
	
	@GetMapping("/")
	public String root() {
		return "redirect:/dashboard/list";
	}
	
	
}
