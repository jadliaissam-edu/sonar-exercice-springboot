package com.jadli.bookstore.level3.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/tools")
public class AdminToolsController {

    @GetMapping("/echo")
    public String echo(@RequestParam String message) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[] {"sh", "-c", "echo " + message});
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return reader.readLine();
        }
    }

    @GetMapping("/status")
    public String status() {
        return "admin tools available";
    }

    @GetMapping("/disk")
    public String disk(@RequestParam(defaultValue = "/tmp") String path) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[] {"sh", "-c", "df -h " + path});
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line).append('\n');
            }
            return out.toString();
        }
    }

    @GetMapping("/env")
    public Map<String, String> environment() {
        Map<String, String> result = new TreeMap<>();
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
