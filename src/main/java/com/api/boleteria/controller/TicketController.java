package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.TicketDetailDTO;
import com.api.boleteria.dto.request.TicketRequestDTO;
import com.api.boleteria.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de boletos (tickets).
 *
 * Expone endpoints para comprar boletos y obtener información sobre los boletos del usuario autenticado.
 * Solo usuarios con rol CLIENT pueden acceder a estos endpoints.
 */

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    //-------------------------------CREATE--------------------------------//
    /**
     * Permite a un usuario con rol CLIENT comprar boletos según la solicitud recibida.
     *
     * @param entity DTO con los datos necesarios para comprar boletos.
     * @return ResponseEntity con la lista de boletos comprados y sus detalles.
     */

    @PostMapping("/comprar")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<TicketDetailDTO>> buyTicket(@RequestBody @Valid TicketRequestDTO entity) {
        return ResponseEntity.ok(ticketService.buyTickets(entity));
    }



    //-------------------------------GET--------------------------------//

    /**
     * Obtiene la lista de boletos asociados al usuario autenticado.
     *
     * @return ResponseEntity con la lista de boletos detallados,
     *         o un estado 204 No Content si no hay boletos.
     */
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<List<TicketDetailDTO>> getTickets() {
        List<TicketDetailDTO> tickets = ticketService.findTicketsFromAuthenticatedUser();
        if (tickets.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tickets);
    }

    /**
     * Obtiene el detalle de un boleto específico por su ID.
     *
     * @param id Identificador único del boleto.
     * @return ResponseEntity con el detalle del boleto solicitado.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<TicketDetailDTO> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findTicketById(id));
    }

}
