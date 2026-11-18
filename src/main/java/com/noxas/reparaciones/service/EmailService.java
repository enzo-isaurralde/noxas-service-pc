package com.noxas.reparaciones.service;

import com.noxas.reparaciones.dto.response.ConsultaResponseDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Servicio de envío de emails transaccionales.
 *
 * Se encarga de mandar dos tipos de correos cuando se crea una consulta:
 *   1. Email al cliente → confirmación con el número de comprobante.
 *   2. Email interno → aviso al equipo para que hagan seguimiento.
 *
 * Los envíos se hacen con @Async para no bloquear la respuesta HTTP del
 * POST /api/consultas (el cliente recibe el "201 Created" al instante).
 *
 * Los cuerpos de los emails son templates Thymeleaf HTML (templates/email/),
 * así mantenemos el diseño separado de la lógica Java.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    // Bean de Spring que sabe conectarse al SMTP (configurado en application.properties)
    @Autowired
    private JavaMailSender mailSender;

    // Motor de templates: renderiza los HTML en templates/email/*.html
    @Autowired
    private TemplateEngine templateEngine;

    // Configuración desde application.properties
    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.mail.notificacion}")
    private String emailNotificacion;


    /**
     * Método público principal.
     *
     * Se llama cuando el cliente crea una consulta nueva desde la landing.
     * Como es @Async, corre en un thread aparte: el POST responde al cliente
     * inmediatamente, sin esperar a que Gmail termine de enviar.
     *
     * Envía dos emails:
     *   - Al cliente (si cargó email en el form).
     *   - Al equipo interno (siempre).
     */
    @Async
    public void enviarConfirmacionConsulta(ConsultaResponseDTO consulta) {

        // ============================================================
        // 1. Email al cliente
        // ============================================================
        // Solo lo mandamos si el cliente cargó un email en el formulario.
        // El campo email es opcional en la landing, así que puede venir null.
        if (consulta.getEmail() != null && !consulta.getEmail().isBlank()) {
            try {
                enviarEmailCliente(consulta);
                log.info("Email de confirmación enviado a {} para consulta {}",
                        consulta.getEmail(), consulta.getNumeroComprobante());
            } catch (Exception e) {
                // No relanzamos la excepción: si falla el mail del cliente,
                // igual queremos intentar el interno y no romper el flujo.
                log.error("Error enviando email de confirmación a {}: {}",
                        consulta.getEmail(), e.getMessage(), e);
            }
        } else {
            log.info("Consulta {} sin email, se omite envío al cliente",
                    consulta.getNumeroComprobante());
        }

        // ============================================================
        // 2. Notificación interna al equipo
        // ============================================================
        // Esta siempre se manda, tenga o no email el cliente.
        // Va al email configurado en app.mail.notificacion.
        try {
            enviarEmailInterno(consulta);
            log.info("Notificación interna enviada para consulta {}",
                    consulta.getNumeroComprobante());
        } catch (Exception e) {
            log.error("Error enviando notificación interna: {}", e.getMessage(), e);
        }
    }


    // ================================================================
    // MÉTODOS PRIVADOS (armado y envío de cada email)
    // ================================================================

    /**
     * Arma y envía el email de confirmación al cliente.
     *
     * Pasos:
     *   1. Prepara las variables que va a usar el template HTML.
     *   2. Renderiza el template Thymeleaf con esas variables.
     *   3. Crea el MimeMessage, le setea from/to/subject/body HTML.
     *   4. Lo manda por SMTP.
     */
    private void enviarEmailCliente(ConsultaResponseDTO consulta)
            throws MessagingException, UnsupportedEncodingException {

        // 1. Contexto de Thymeleaf: variables disponibles como ${...} en el HTML
        Context ctx = new Context(new Locale("es", "AR"));
        ctx.setVariable("consulta", consulta);
        ctx.setVariable("costoFormateado", formatearPrecio(consulta.getCostoEstimado()));
        ctx.setVariable("esGratis", consulta.getCostoEstimado() != null
                && consulta.getCostoEstimado().compareTo(BigDecimal.ZERO) == 0);
        ctx.setVariable("fechaFormateada", consulta.getCreadoEn() != null
                ? consulta.getCreadoEn().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "");

        // 2. Renderiza templates/email/consulta-recibida.html → String con HTML final
        String html = templateEngine.process("email/consulta-recibida", ctx);

        // 3. Armamos el mensaje MIME (soporta HTML y UTF-8)
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        helper.setFrom(fromEmail, fromName);
        helper.setTo(consulta.getEmail());
        helper.setSubject("Recibimos tu consulta " + consulta.getNumeroComprobante() + " — NOXAS Service PC");
        helper.setText(html, true);  // true = es HTML, no texto plano

        // 4. Enviar por SMTP
        mailSender.send(mensaje);
    }


    /**
     * Arma y envía el email de notificación interna al equipo.
     *
     * Es más simple que el del cliente: no tiene botón de WhatsApp ni
     * el bloque grande del comprobante. Solo lista los datos clave
     * y un botón para abrir el panel admin.
     */
    private void enviarEmailInterno(ConsultaResponseDTO consulta)
            throws MessagingException, UnsupportedEncodingException {

        Context ctx = new Context(new Locale("es", "AR"));
        ctx.setVariable("consulta", consulta);
        ctx.setVariable("costoFormateado", formatearPrecio(consulta.getCostoEstimado()));

        String html = templateEngine.process("email/notificacion-interna", ctx);

        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
        helper.setFrom(fromEmail, fromName);
        helper.setTo(emailNotificacion);
        helper.setSubject("🔔 Nueva consulta " + consulta.getNumeroComprobante()
                + " — " + consulta.getBarrioNombre());
        helper.setText(html, true);

        mailSender.send(mensaje);
    }


    /**
     * Formatea un BigDecimal como precio en formato argentino.
     * Ej: 25000.00 → "$25.000"
     * Ej: 0.00     → "$0"
     * Ej: null     → "-"
     */
    private String formatearPrecio(BigDecimal monto) {
        if (monto == null) return "-";
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("es", "AR"));
        formatter.setMaximumFractionDigits(0);
        return "$" + formatter.format(monto);
    }
}