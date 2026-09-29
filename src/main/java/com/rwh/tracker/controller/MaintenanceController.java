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

/**
 * Handles logging maintenance for components.
 */
@Controller
public class MaintenanceController {

    private final SystemService systemService;

    public MaintenanceController(SystemService systemService) {
        this.systemService = systemService;
    }

    // ── Maintenance log form ──────────────────────────────────────────────────

    @GetMapping("/components/{id}/logs/new")
    public String newLogForm(@PathVariable Long id, Model model) {
        Component component = systemService.getComponent(id);
        model.addAttribute("component", component);
        model.addAttribute("maintenanceLog", new MaintenanceLog());
        return "components/maintenance-log-form";
    }

    // ── Submit maintenance log ────────────────────────────────────────────────

    @PostMapping("/components/{id}/logs")
    public String createLog(@PathVariable Long id,
                            @Valid @ModelAttribute("maintenanceLog") MaintenanceLog log,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Component component = systemService.getComponent(id);
            model.addAttribute("component", component);
            return "components/maintenance-log-form";
        }
        MaintenanceLog saved = systemService.logMaintenance(id, log);
        redirectAttributes.addFlashAttribute("successMessage",
                "Maintenance logged successfully for '" + saved.getComponent().getName() + "'!");
        return "redirect:/systems/" + saved.getComponent().getSystem().getId();
    }
}
