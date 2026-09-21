package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AjaxViewController {
    @GetMapping("/ajax/categories") String categories() { return "categories"; }
    @GetMapping("/ajax/products") String products() { return "products"; }
}
