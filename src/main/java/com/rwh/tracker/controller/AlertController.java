package com.rwh.tracker.controller;

import com.rwh.tracker.model.Alert;
import com.rwh.tracker.service.SystemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/alerts")
public class AlertController {

    private final SystemService systemService;

    public AlertController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping
    public String listAlerts(Model model) {
        List<Alert> alerts = systemService.getPendingAndOverdueAlerts();
        model.addAttribute("alerts", alerts);
        return "alerts/alerts-list";
    }
}
