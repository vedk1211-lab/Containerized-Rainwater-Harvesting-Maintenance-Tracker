package com.rwh.tracker.controller;

import com.rwh.tracker.model.Component;
import com.rwh.tracker.model.MaintenanceLog;
import com.rwh.tracker.service.SystemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/components/{componentId}/logs")
public class MaintenanceLogController {

    private final SystemService systemService;

    public MaintenanceLogController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/new")
    public String newMaintenanceLogForm(@PathVariable Long componentId, Model model) {
        Component component = systemService.getComponent(componentId);
        
        MaintenanceLog log = new MaintenanceLog();
        log.setPerformedAt(LocalDate.now()); // Default to today
        
        model.addAttribute("component", component);
        model.addAttribute("maintenanceLog", log);
        
        return "components/maintenance-log-form";
    }

    @PostMapping
    public String saveMaintenanceLog(@PathVariable Long componentId,
                                     @Valid @ModelAttribute("maintenanceLog") MaintenanceLog log,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Component component = systemService.getComponent(componentId);
            model.addAttribute("component", component);
            return "components/maintenance-log-form";
        }
        
        systemService.logMaintenance(componentId, log);
        
        Component component = systemService.getComponent(componentId);
        redirectAttributes.addFlashAttribute("successMessage", 
                "Maintenance logged successfully for " + component.getName());
                
        return "redirect:/systems/" + component.getSystem().getId();
    }
}
