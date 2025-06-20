package com.api.boleteria.service;

import com.api.boleteria.dto.detail.TicketDetailDTO;
import com.api.boleteria.dto.request.TicketRequestDTO;
import com.api.boleteria.exception.AccesDeniedException;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Card;
import com.api.boleteria.model.Ticket;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.User;
import com.api.boleteria.repository.ICardRepository;
import com.api.boleteria.repository.ITicketRepository;
import com.api.boleteria.repository.IFunctionRepository;
import com.api.boleteria.repository.IUserRepository;
import com.api.boleteria.validators.TicketValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Servicio para gestionar operaciones relacionadas con tickets.
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    private final ITicketRepository ticketRepository;
    private final ICardRepository cardRepository;
    private final IUserRepository userRepository;
    private final IFunctionRepository functionRepository;
    private final UserService userService;

    public static final double TICKET_PRICE = 2500.0;


    //-------------------------------SAVE--------------------------------//

    /**
     * Crea uno o varios tickets para una función específica.
     *
     * Este método realiza las siguientes operaciones de manera transaccional:
     * - Verifica disponibilidad de entradas.
     * - Verifica saldo en la tarjeta del usuario.
     * - Descuenta saldo de la tarjeta.
     * - Reduce la capacidad disponible de la función.
     * - Crea los tickets correspondientes y los asocia al usuario.
     *
     * Si alguna de estas operaciones falla, la transacción se revierte y no se guarda ningún cambio.
     *
     * @param dto DTO con los datos de la compra (ID de función y cantidad).
     * @return Lista de TicketDetailDTO con los tickets comprados.
     * @throws NotFoundException si no se encuentra la función o la tarjeta del usuario.
     * @throws BadRequestException si no hay capacidad suficiente o fondos en la tarjeta.
     */
    @Transactional
    public List<TicketDetailDTO> buyTickets(TicketRequestDTO dto) {
        TicketValidator.validateFields(dto);

        User user = userService.findAuthenticatedUser();

        Function function = functionRepository.findById(dto.getFunctionId())
                .orElseThrow(() -> new NotFoundException("Función no encontrada."));
        TicketValidator.validateCapacity(function, dto.getQuantity());

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("El usuario " + user.getUsername() + " no tiene una tarjeta registrada."));
        TicketValidator.validateCardBalance(card, dto.getQuantity());

        double totalAmount = TICKET_PRICE * dto.getQuantity();

        card.setBalance(card.getBalance() - totalAmount);
        function.setAvailableCapacity(function.getAvailableCapacity() - dto.getQuantity());
        cardRepository.save(card);
        functionRepository.save(function);

        List<Ticket> createdTickets = IntStream.range(0, dto.getQuantity())
                .mapToObj(i -> mapToEntity(user, function))
                .map(ticketRepository::save)
                .toList();

        user.getTickets().addAll(createdTickets);
        userRepository.save(user);

        return createdTickets.stream()
                .map(this::mapToDetailDTO)
                .toList();
    }




    //-------------------------------FIND--------------------------------//

    /**
     * Obtiene todos los tickets asociados al usuario autenticado.
     *
     * @return Lista de TicketDetailDTO con los tickets del usuario.
     */
    public List<TicketDetailDTO> findTicketsFromAuthenticatedUser() {
        User user = userService.findAuthenticatedUser();

        return user.getTickets().stream()
                .map(this::mapToDetailDTO)
                .toList();
    }

    /**
     * Obtiene un ticket específico por su ID, validando que el ID sea válido y que el ticket pertenezca al usuario autenticado.
     *
     * @param ticketId ID del ticket a buscar.
     * @return TicketDetailDTO con los datos del ticket.
     * @throws IllegalArgumentException si el ID del ticket es nulo o menor o igual a cero.
     * @throws NotFoundException si no se encuentra un ticket con el ID especificado.
     * @throws AccesDeniedException si el ticket no pertenece al usuario autenticado.
     */
    public TicketDetailDTO findTicketById(Long ticketId) {
        User user = userService.findAuthenticatedUser();
        TicketValidator.validateTicketId(ticketId);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("No se encontró el ticket con ID: " + ticketId));

        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new AccesDeniedException("No tiene permiso para ver este ticket.");
        }

        return mapToDetailDTO(ticket);
    }



    //-------------------------------MAPS--------------------------------//

    /**
     * Convierte una entidad Ticket a un DTO detallado.
     * @param ticket entidad de ticket a convertir
     * @return TicketDetailDTO con los datos relevantes del ticket
     */
    private TicketDetailDTO mapToDetailDTO(Ticket ticket) {
        return new TicketDetailDTO(
                ticket.getId(),
                ticket.getPurchaseDateTime().toLocalDate().toString(),
                ticket.getFunction().getMovie().getTitle(),
                ticket.getFunction().getCinema().getId(),
                ticket.getPurchaseDateTime().toLocalTime().toString(),
                ticket.getTicketPrice()
        );
    }

    private Ticket mapToEntity(User user, Function function) {
        Ticket ticket = new Ticket();
        ticket.setTicketPrice(TICKET_PRICE);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setUser(user);
        ticket.setFunction(function);
        return ticket;
    }

}
