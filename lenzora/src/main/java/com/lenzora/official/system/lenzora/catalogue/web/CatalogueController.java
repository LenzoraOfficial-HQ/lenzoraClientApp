package com.lenzora.official.system.lenzora.catalogue.web;

import com.lenzora.official.system.lenzora.catalogue.application.FrameService;
import com.lenzora.official.system.lenzora.catalogue.application.LensOptionService;
import com.lenzora.official.system.lenzora.catalogue.application.SupplierService;
import com.lenzora.official.system.lenzora.catalogue.domain.Frame;
import com.lenzora.official.system.lenzora.catalogue.domain.LensOption;
import com.lenzora.official.system.lenzora.catalogue.domain.Supplier;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.FrameRepository;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.LensOptionRepository;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.SupplierRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** The catalogue screens: list the reference data, then update one row. */
@Controller
@RequestMapping("/catalogue")
public class CatalogueController {

    /** Updates a frame row. */
    private final FrameService frameService;

    /** Updates a lens option row. */
    private final LensOptionService lensOptionService;

    /** Updates a supplier row. */
    private final SupplierService supplierService;

    /** Reads the frames. */
    private final FrameRepository frameRepository;

    /** Reads the lens options. */
    private final LensOptionRepository lensOptionRepository;

    /** Reads the suppliers. */
    private final SupplierRepository supplierRepository;

    public CatalogueController(FrameService pFrameService,
                               LensOptionService pLensOptionService,
                               SupplierService pSupplierService,
                               FrameRepository pFrameRepository,
                               LensOptionRepository pLensOptionRepository,
                               SupplierRepository pSupplierRepository) {

        this.frameService = pFrameService;
        this.lensOptionService = pLensOptionService;
        this.supplierService = pSupplierService;
        this.frameRepository = pFrameRepository;
        this.lensOptionRepository = pLensOptionRepository;
        this.supplierRepository = pSupplierRepository;
    }

    /**
     * Shows the three tables, or the form of the row being edited.
     * @param pEdit frame, lensOption or supplier
     * @param pId the row being edited, ignored when pEdit is empty
     * @param model the data handed to catalogue.html
     * @return the name of the page to show
     */
    @GetMapping
    public String show(@RequestParam(name = "edit", required = false) String pEdit,
                       @RequestParam(name = "id", required = false) Long pId,
                       Model model) {

        model.addAttribute("frames", frameRepository.findAll());
        model.addAttribute("lensOptions", lensOptionRepository.findAll());
        model.addAttribute("suppliers", supplierRepository.findAll());

        model.addAttribute("frameForm", frameInEdit(pEdit, pId));
        model.addAttribute("lensForm", lensOptionInEdit(pEdit, pId));
        model.addAttribute("supplierForm", supplierInEdit(pEdit, pId));

        return "catalogue";
    }

    @PostMapping("/frames/{pId}")
    public String saveFrame(@PathVariable Long pId,
                            @ModelAttribute("frameForm") Frame pForm,
                            BindingResult pErrors,
                            @RequestParam(name = "supplierId", required = false) Long pSupplierId,
                            RedirectAttributes pRedirect) {

        if (pErrors.hasErrors()) {
            return backToFrame(pId, firstProblem(pErrors), pRedirect);
        }

        // The form posts the supplier id, so the row is looked up here.
        Supplier supplier = null;
        if (pSupplierId != null) {
            supplier = supplierRepository.findById(pSupplierId).orElse(null);
        }
        if (supplier == null) {
            return backToFrame(pId, "Please choose a supplier for the frame.", pRedirect);
        }
        pForm.setSupplier(supplier);

        try {
            frameService.updateFrame(pId, pForm);
        } catch (DataIntegrityViolationException problem) {
            return backToFrame(pId, "The database refused these values. Check the brand, model, colour and price.", pRedirect);
        } catch (RuntimeException problem) {
            return backToFrame(pId, problem.getMessage(), pRedirect);
        }

        pRedirect.addFlashAttribute("successMessage", "Frame " + pForm.getBrand() + " " + pForm.getModel() + " updated successfully.");
        return "redirect:/catalogue";
    }

    @PostMapping("/lens-options/{pId}")
    public String saveLensOption(@PathVariable Long pId,
                                 @ModelAttribute("lensForm") LensOption pForm,
                                 BindingResult pErrors,
                                 RedirectAttributes pRedirect) {

        if (pErrors.hasErrors()) {
            return backToLensOption(pId, firstProblem(pErrors), pRedirect);
        }

        try {
            lensOptionService.updateLensOption(pId, pForm);
        } catch (DataIntegrityViolationException problem) {
            return backToLensOption(pId, "The database refused these values. Check the code and the extra charge.", pRedirect);
        } catch (RuntimeException problem) {
            return backToLensOption(pId, problem.getMessage(), pRedirect);
        }

        pRedirect.addFlashAttribute("successMessage", "Lens option " + pForm.getLabel() + " updated successfully.");
        return "redirect:/catalogue";
    }

    @PostMapping("/suppliers/{pId}")
    public String saveSupplier(@PathVariable Long pId,
                               @ModelAttribute("supplierForm") Supplier pForm,
                               BindingResult pErrors,
                               RedirectAttributes pRedirect) {

        if (pErrors.hasErrors()) {
            return backToSupplier(pId, firstProblem(pErrors), pRedirect);
        }

        try {
            supplierService.updateSupplier(pId, pForm);
        } catch (DataIntegrityViolationException problem) {
            return backToSupplier(pId, "The database refused these values. Check the name and the email address.", pRedirect);
        } catch (RuntimeException problem) {
            return backToSupplier(pId, problem.getMessage(), pRedirect);
        }

        pRedirect.addFlashAttribute("successMessage", "Supplier " + pForm.getSupplierName() + " updated successfully.");
        return "redirect:/catalogue";
    }

    /** Reads the frame being edited, or null while the whole table is shown. */
    private Frame frameInEdit(String pEdit, Long pId) {
        if (pId == null || !"frame".equals(pEdit)) {
            return null;
        }
        return frameRepository.findById(pId).orElse(null);
    }

    /** Reads the lens option being edited, or null while the whole table is shown. */
    private LensOption lensOptionInEdit(String pEdit, Long pId) {
        if (pId == null || !"lensOption".equals(pEdit)) {
            return null;
        }
        return lensOptionRepository.findById(pId).orElse(null);
    }

    /** Reads the supplier being edited, or null while the whole table is shown. */
    private Supplier supplierInEdit(String pEdit, Long pId) {
        if (pId == null || !"supplier".equals(pEdit)) {
            return null;
        }
        return supplierRepository.findById(pId).orElse(null);
    }

    /** Sends the user back to the frame form with one message on it. */
    private String backToFrame(Long pId, String pMessage, RedirectAttributes pRedirect) {
        System.out.println("Frame " + pId + " not updated: " + pMessage);
        pRedirect.addFlashAttribute("errorMessage", pMessage);
        return "redirect:/catalogue?edit=frame&id=" + pId;
    }

    /** Sends the user back to the lens option form with one message on it. */
    private String backToLensOption(Long pId, String pMessage, RedirectAttributes pRedirect) {
        System.out.println("Lens option " + pId + " not updated: " + pMessage);
        pRedirect.addFlashAttribute("errorMessage", pMessage);
        return "redirect:/catalogue?edit=lensOption&id=" + pId;
    }

    /** Sends the user back to the supplier form with one message on it. */
    private String backToSupplier(Long pId, String pMessage, RedirectAttributes pRedirect) {
        System.out.println("Supplier " + pId + " not updated: " + pMessage);
        pRedirect.addFlashAttribute("errorMessage", pMessage);
        return "redirect:/catalogue?edit=supplier&id=" + pId;
    }

    /** Turns the first binding problem into a short sentence. */
    private String firstProblem(BindingResult pErrors) {
        if (pErrors.getFieldErrors().isEmpty()) {
            return "The values typed on the form could not be read.";
        }
        FieldError first = pErrors.getFieldErrors().get(0);
        return "The value typed for " + first.getField() + " could not be used.";
    }
}
