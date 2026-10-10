package com.lenzora.official.system.lenzora.catalogue.web;

import com.lenzora.official.system.lenzora.catalogue.application.SupplierService;
import com.lenzora.official.system.lenzora.catalogue.domain.Supplier;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.SupplierRepository;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Admin portal supplier registration and listing screens. */
@Controller
@RequestMapping("/admin/suppliers")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupplierController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminSupplierController.class);

    private final SupplierService supplierService;
    private final SupplierRepository supplierRepository;

    public AdminSupplierController(SupplierService supplierService,
                                   SupplierRepository supplierRepository) {
        this.supplierService = supplierService;
        this.supplierRepository = supplierRepository;
    }

    @GetMapping
    public String showSuppliers(Model model) {
        preparePage(model);
        return "catalogue/suppliers";
    }

    @PostMapping
    public String registerSupplier(@Valid @ModelAttribute("supplierForm") Supplier supplier,
                                   BindingResult errors,
                                   Model model,
                                   RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            model.addAttribute("errorMessage", "Please correct the supplier details below.");
            model.addAttribute("supplierList", supplierRepository.findAll());
            return "catalogue/suppliers";
        }

        try {
            supplierService.createSupplier(supplier);
        } catch (DataIntegrityViolationException problem) {
            LOGGER.error("Could not register supplier '{}'.", supplier.getSupplierName(), problem);
            model.addAttribute("errorMessage", "The database could not save this supplier. Check the supplied details.");
            model.addAttribute("supplierList", supplierRepository.findAll());
            return "catalogue/suppliers";
        }

        redirect.addFlashAttribute("successMessage",
                "Supplier " + supplier.getSupplierName() + " registered successfully.");
        return "redirect:/admin/suppliers";
    }

    private void preparePage(Model model) {
        model.addAttribute("supplierList", supplierRepository.findAll());
        model.addAttribute("supplierForm", new Supplier());
    }
}
