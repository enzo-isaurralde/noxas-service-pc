package com.noxas.reparaciones.service;

import com.noxas.reparaciones.domain.BarrioCobertura;
import com.noxas.reparaciones.domain.Consulta;
import com.noxas.reparaciones.domain.EstadoConsulta;
import com.noxas.reparaciones.dto.request.ConsultaRequestDTO;
import com.noxas.reparaciones.dto.response.ConsultaResponseDTO;
import com.noxas.reparaciones.dto.response.CotizacionResponseDTO;
import com.noxas.reparaciones.mapper.ConsultaMapper;
import com.noxas.reparaciones.repository.BarrioCoberturaRepository;
import com.noxas.reparaciones.repository.ConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Orquesta la creación y consulta de Consultas: valida horario,
 * resuelve el costo por barrio, genera el número de comprobante
 * y persiste todo.
 */
@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private BarrioCoberturaRepository barrioCoberturaRepository;

    @Autowired
    private CoberturaService coberturaService;

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private ConsultaMapper consultaMapper;

    @Autowired
    private EmailService emailService;

    /**
     * Crea una nueva consulta a partir de lo que mandó el formulario.
     * Falla con excepción si el horario pedido está fuera de atención
     * o si el barrio no existe/está inactivo.
     */
    @Transactional
    public ConsultaResponseDTO crearConsulta(ConsultaRequestDTO request) {

        // 1. Validar que el horario pedido esté dentro de tu atención.
        //    Si falla, tira FueraDeHorarioException y no sigue.
        horarioService.validarHorario(request.getFechaHoraSolicitada());

        // 2. Resolver costo y validez del barrio (reutilizamos CoberturaService
        //    para no duplicar la lógica de "barrio activo o no")
        CotizacionResponseDTO cotizacion = coberturaService.cotizarPorBarrio(request.getBarrioId());

        // Necesitamos también la entidad completa del barrio para la relación
        BarrioCobertura barrio = barrioCoberturaRepository.findById(request.getBarrioId())
                .orElseThrow(() -> new IllegalArgumentException("El barrio seleccionado no es válido"));

        // 3. Generar un número de comprobante único
        String numeroComprobante = generarNumeroComprobante();

        // 4. Armar la entidad con Builder (gracias a Lombok en Consulta)
        Consulta consulta = Consulta.builder()
                .numeroComprobante(numeroComprobante)
                .nombreCliente(request.getNombreCliente())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .barrio(barrio)
                .direccion(request.getDireccion())
                .costoEstimado(cotizacion.getCostoEstimado())
                .equipo(request.getEquipo())
                .descripcionProblema(request.getDescripcionProblema())
                .estado(EstadoConsulta.PENDIENTE)
                .build();

        // 5. Guardar en la base
        Consulta consultaGuardada = consultaRepository.save(consulta);

        // 6. Convertir a DTO de respuesta
        ConsultaResponseDTO response = consultaMapper.toResponseDTO(consultaGuardada);

        // 7. ✨ NUEVO: Enviar email de confirmación (async, no bloquea la respuesta)
        emailService.enviarConfirmacionConsulta(response);

        return response;
    }

    /**
     * Genera un número tipo RC-2026-0001, incremental por año.
     * Si por alguna colisión ya existe (muy poco probable), sigue
     * incrementando hasta encontrar uno libre.
     */
    private String generarNumeroComprobante() {
        int anioActual = LocalDate.now().getYear();
        long siguienteNumero = consultaRepository.count() + 1;
        String numeroComprobante = String.format("RC-%d-%04d", anioActual, siguienteNumero);

        while (consultaRepository.existsByNumeroComprobante(numeroComprobante)) {
            siguienteNumero++;
            numeroComprobante = String.format("RC-%d-%04d", anioActual, siguienteNumero);
        }

        return numeroComprobante;
    }

    // --- Métodos para el panel admin ---

    /**
     * Lista todas las consultas, las más recientes primero.
     */
    public List<ConsultaResponseDTO> listarTodas() {
        return consultaRepository.findAllByOrderByCreadoEnDesc()
                .stream()
                .map(consultaMapper::toResponseDTO)
                .toList();
    }

    /**
     * Filtra consultas por estado (ej. solo las PENDIENTE).
     */
    public List<ConsultaResponseDTO> listarPorEstado(EstadoConsulta estado) {
        return consultaRepository.findByEstadoOrderByCreadoEnDesc(estado)
                .stream()
                .map(consultaMapper::toResponseDTO)
                .toList();
    }

    /**
     * Cambia el estado de una consulta (ej. de PENDIENTE a CONTACTADO).
     */
    @Transactional
    public ConsultaResponseDTO cambiarEstado(Long id, EstadoConsulta nuevoEstado) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada"));

        consulta.setEstado(nuevoEstado);
        // No hace falta guardar explícitamente: al estar dentro de una
        // transacción, JPA detecta el cambio y hace el UPDATE solo
        // (esto se llama "dirty checking")

        return consultaMapper.toResponseDTO(consulta);
    }

    /**
     * Busca una consulta por su id para la vista de detalle del panel.
     */
    public ConsultaResponseDTO buscarPorId(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada con id: " + id));
        return consultaMapper.toResponseDTO(consulta);
    }
    /**
     * Cuenta cuántas consultas hay en cada estado, para mostrar en el panel.
     */
    public long contarPorEstado(EstadoConsulta estado) {
        return consultaRepository.findByEstadoOrderByCreadoEnDesc(estado).size();
    }

    /**
     * Cuenta el total de consultas.
     */
    public long contarTodas() {
        return consultaRepository.count();
    }
}