package com.noxas.reparaciones.controller.admin;

import com.noxas.reparaciones.domain.EstadoConsulta;
import com.noxas.reparaciones.dto.response.ConsultaResponseDTO;
import com.noxas.reparaciones.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador del panel de administración.
 * Todas las rutas bajo /admin/** (excepto /admin/login) requieren
 * estar logueado. La protección la hace SecurityConfig.
 */
@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    @Autowired
    private ConsultaService consultaService;

    /**
     * Pantalla de login del panel.
     * Si ya está logueado, redirige a la lista.
     */
    @GetMapping("/login")
    public String login(Authentication auth) {
        if (auth != null && auth.isAuthenticated()
                && !auth.getClass().getSimpleName().equals("AnonymousAuthenticationToken")) {
            return "redirect:/admin/consultas";
        }
        return "admin/login";
    }

    /**
     * Listado de consultas con filtro opcional por estado.
     * URL: /admin/consultas?estado=PENDIENTE
     */
    @GetMapping("/consultas")
    public String listarConsultas(
            @RequestParam(value = "estado", required = false) String estadoFiltro,
            Model model,
            Authentication auth) {

        // Elegir la lista según el filtro
        List<ConsultaResponseDTO> consultas;
        EstadoConsulta estadoSeleccionado = null;

        if (estadoFiltro != null && !estadoFiltro.isBlank()) {
            try {
                estadoSeleccionado = EstadoConsulta.valueOf(estadoFiltro.toUpperCase());
                consultas = consultaService.listarPorEstado(estadoSeleccionado);
            } catch (IllegalArgumentException e) {
                consultas = consultaService.listarTodas();
            }
        } else {
            consultas = consultaService.listarTodas();
        }

        // Contadores para las pestañas
        long totalPendientes = consultaService.contarPorEstado(EstadoConsulta.PENDIENTE);
        long totalContactados = consultaService.contarPorEstado(EstadoConsulta.CONTACTADO);
        long totalResueltos = consultaService.contarPorEstado(EstadoConsulta.RESUELTA);
        long totalGeneral = consultaService.contarTodas();

        model.addAttribute("consultas", consultas);
        model.addAttribute("estadoFiltro", estadoSeleccionado != null ? estadoSeleccionado.name() : "TODAS");
        model.addAttribute("totalPendientes", totalPendientes);
        model.addAttribute("totalContactados", totalContactados);
        model.addAttribute("totalResueltos", totalResueltos);
        model.addAttribute("totalGeneral", totalGeneral);
        model.addAttribute("usuario", auth.getName());

        return "admin/lista";
    }

    /**
     * Detalle de una consulta.
     */
    @GetMapping("/consultas/{id}")
    public String verDetalle(@PathVariable Long id, Model model, Authentication auth) {
        ConsultaResponseDTO consulta = consultaService.buscarPorId(id);
        model.addAttribute("consulta", consulta);
        model.addAttribute("usuario", auth.getName());
        return "admin/detalle";
    }

    /**
     * Cambiar el estado de una consulta.
     */
    @PostMapping("/consultas/{id}/estado")
    public String cambiarEstado(
            @PathVariable Long id,
            @RequestParam("estado") String nuevoEstado,
            RedirectAttributes redirectAttrs) {

        try {
            EstadoConsulta estado = EstadoConsulta.valueOf(nuevoEstado.toUpperCase());
            consultaService.cambiarEstado(id, estado);
            redirectAttrs.addFlashAttribute("mensaje", "Estado actualizado correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttrs.addFlashAttribute("error", "No se pudo cambiar el estado: " + e.getMessage());
        }

        return "redirect:/admin/consultas/" + id;
    }
}