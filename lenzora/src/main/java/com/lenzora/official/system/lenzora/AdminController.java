package com.lenzora.official.system.lenzora;

import com.lenzora.official.system.lenzora.catalogue.infrastructure.FrameRepository;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.LensOptionRepository;
import com.lenzora.official.system.lenzora.catalogue.infrastructure.SupplierRepository;
import com.lenzora.official.system.lenzora.config.HospitalSettings;
import com.lenzora.official.system.lenzora.ordering.Order;
import com.lenzora.official.system.lenzora.ordering.OrderRepository;
import com.lenzora.official.system.lenzora.ordering.OrderResult;
import com.lenzora.official.system.lenzora.ordering.OrderService;
import com.lenzora.official.system.lenzora.repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * The administration portal. Every route here is limited to accounts
 * whose app_user.user_role is ADMIN - see SecurityConfig.
 */
@Controller
public class AdminController {

    /** Reads the accounts of the platform. */
    private final UserRepository userRepository;

    /** Reads the orders. */
    private final OrderRepository orderRepository;

    /** Moves an order to its next status. */
    private final OrderService orderService;

    /** Reads the frames of the catalogue. */
    private final FrameRepository frameRepository;

    /** Reads the lens options of the catalogue. */
    private final LensOptionRepository lensOptionRepository;

    /** Reads the suppliers of the catalogue. */
    private final SupplierRepository supplierRepository;

    /** Keeps the hospital details the orders are sent to. */
    private final HospitalSettings hospitalSettings;

    public AdminController(UserRepository pUserRepository, OrderRepository pOrderRepository,
                           OrderService pOrderService, FrameRepository pFrameRepository,
                           LensOptionRepository pLensOptionRepository, SupplierRepository pSupplierRepository,
                           HospitalSettings pHospitalSettings) {

        this.userRepository = pUserRepository;
        this.orderRepository = pOrderRepository;
        this.orderService = pOrderService;
        this.frameRepository = pFrameRepository;
        this.lensOptionRepository = pLensOptionRepository;
        this.supplierRepository = pSupplierRepository;
        this.hospitalSettings = pHospitalSettings;
    }

    /**
     * Shows the admin portal home with the current totals.
     *
     * @param model the data handed to admin-dashboard.html
     * @param authentication the signed-in administrator
     * @return the name of the page to show
     */
    @GetMapping("/admin")
    public String showAdminHome(Model model, Authentication authentication) {

        model.addAttribute("adminEmail", authentication.getName());
        model.addAttribute("userCount", userRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("reviewCount", orderRepository.countByStatus(Order.OrderStatus.SENT_FOR_REVIEW));
        model.addAttribute("frameCount", frameRepository.count());
        model.addAttribute("lensOptionCount", lensOptionRepository.count());
        model.addAttribute("supplierCount", supplierRepository.count());

        return "admin-dashboard";
    }

    /**
     * Shows the orders the orthoptists sent for admin review, together with the
     * orders that were already reviewed and are ready to place.
     *
     * @param model the data handed to admin-orders.html
     * @return the name of the page to show
     */
    @GetMapping("/admin/orders")
    public String showOrders(Model model) {

        model.addAttribute("reviewQueue",
                orderRepository.findByStatusOrderByOrderDateDescIdDesc(Order.OrderStatus.SENT_FOR_REVIEW));
        model.addAttribute("placementQueue",
                orderRepository.findByStatusOrderByOrderDateDescIdDesc(Order.OrderStatus.REVIEWED));

        return "admin-orders";
    }

    /**
     * Marks an order as reviewed.
     *
     * @param pId the id of the order
     * @param redirect what to show after the change
     * @return a redirect back to the review queue
     */
    @PostMapping("/admin/orders/{pId}/review")
    public String review(@PathVariable Long pId, RedirectAttributes redirect) {
        return moveTo(pId, Order.OrderStatus.REVIEWED, "reviewed", redirect);
    }

    /**
     * Places a reviewed order with the supplier.
     *
     * @param pId the id of the order
     * @param redirect what to show after the change
     * @return a redirect back to the review queue
     */
    @PostMapping("/admin/orders/{pId}/place")
    public String place(@PathVariable Long pId, RedirectAttributes redirect) {
        return moveTo(pId, Order.OrderStatus.PLACED, "placed", redirect);
    }

    /**
     * Cancels an order that is still waiting for review.
     *
     * @param pId the id of the order
     * @param redirect what to show after the change
     * @return a redirect back to the review queue
     */
    @PostMapping("/admin/orders/{pId}/cancel")
    public String cancel(@PathVariable Long pId, RedirectAttributes redirect) {
        return moveTo(pId, Order.OrderStatus.CANCELLED, "cancelled", redirect);
    }

    /**
     * Changes one order to a new status and reports the outcome on the next page.
     *
     * @param pId the id of the order
     * @param pNext the status to move the order to
     * @param pDone the word used in the success message
     * @param redirect what to show after the change
     * @return a redirect back to the review queue
     */
    private String moveTo(Long pId, Order.OrderStatus pNext, String pDone, RedirectAttributes redirect) {

        OrderResult result = orderService.changeStatus(pId, pNext);

        if (result.isSaved()) {
            redirect.addFlashAttribute("successMessage",
                    "Order " + result.getOrder().getOrderNumber() + " is now " + pDone + ".");
        } else {
            redirect.addFlashAttribute("errorMessage", result.getMessage());
        }

        return "redirect:/admin/orders";
    }

    /**
     * Shows the account list.
     *
     * @return the name of the page to show
     */
    @GetMapping("/users")
    public String showUsers() {
        return "users";
    }

    /**
     * Shows the reports.
     *
     * @return the name of the page to show
     */
    @GetMapping("/reports")
    public String showReports() {
        return "reports";
    }

    /**
     * Shows the settings, together with the hospital details already saved.
     *
     * @param model the data handed to settings.html
     * @return the name of the page to show
     */
    @GetMapping("/settings")
    public String showSettings(Model model) {

        model.addAttribute("hospital", hospitalSettings);

        return "settings";
    }

    /**
     * Saves the hospital details the order form shows.
     *
     * @param pHospitalName the hospital or clinic the orders go to
     * @param pLocation the branch the orders are sent from
     * @param pAddress the address shown with the hospital
     * @param pPhone the contact number shown with the hospital
     * @param redirect what to show after the save
     * @return a redirect back to the settings page
     */
    @PostMapping("/settings/hospital")
    public String saveHospital(@RequestParam(name = "hospitalName", required = false) String pHospitalName,
                               @RequestParam(name = "location", required = false) String pLocation,
                               @RequestParam(name = "address", required = false) String pAddress,
                               @RequestParam(name = "phone", required = false) String pPhone,
                               RedirectAttributes redirect) {

        String problem = hospitalSettings.save(pHospitalName, pLocation, pAddress, pPhone);

        if (problem.isEmpty()) {
            redirect.addFlashAttribute("successMessage",
                    "Hospital details saved. Every new order will show them.");
        } else {
            redirect.addFlashAttribute("errorMessage", problem);
        }

        return "redirect:/settings";
    }
}
