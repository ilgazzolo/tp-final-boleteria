package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.CardDetailDTO;
import com.api.boleteria.dto.request.CardRequestDTO;
import com.api.boleteria.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Controlador REST para la gestión de la tarjeta de un cliente autenticado.
 *
 * Permite realizar operaciones como obtener la tarjeta, crearla, actualizarla,
 * recargar saldo, consultar saldo y eliminar la tarjeta asociada al usuario autenticado.
 *
 * Todas las operaciones requieren que el usuario tenga el rol 'CLIENT'.
 */

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;


     //-------------------------------CREATE--------------------------------//

    /**
     * Crea una nueva tarjeta para el usuario autenticado.
     *
     * @param entity DTO con los datos necesarios para crear la tarjeta.
     * @return ResponseEntity con el detalle de la tarjeta creada.
     */

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CardDetailDTO> create(@RequestBody @Valid CardRequestDTO entity) {
        return ResponseEntity.ok(cardService.save(entity));
    }



    //-------------------------------GET--------------------------------//

    /**
     * Obtiene la tarjeta asociada al usuario actualmente autenticado.
     *
     * @return ResponseEntity con el detalle de la tarjeta.
     */

    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CardDetailDTO> get() {
        return ResponseEntity.ok(cardService.findFromAuthenticatedUser());
    }

    /**
     * Obtiene el saldo actual disponible en la tarjeta del usuario autenticado.
     *
     * @return ResponseEntity con el saldo como un valor Double.
     */

    @GetMapping("/balance")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Double> getBalance() {
        return ResponseEntity.ok(cardService.getBalance());
    }



    //-------------------------------UPDATE--------------------------------//

    /**
     * Actualiza la información de la tarjeta asociada al usuario autenticado.
     *
     * @param entity DTO con los nuevos datos para la tarjeta.
     * @return ResponseEntity con el detalle actualizado de la tarjeta.
     */

    @PutMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CardDetailDTO> update(@RequestBody @Valid CardRequestDTO entity) {
        return ResponseEntity.ok(cardService.updateAuthenticatedUserCard(entity));
    }


    /**
     * Recarga el saldo de la tarjeta del usuario autenticado con el monto especificado.
     *
     * @param amount Monto a recargar en la tarjeta.
     * @return ResponseEntity con el detalle actualizado de la tarjeta.
     */

    @PatchMapping("/recharge")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CardDetailDTO> recharge(@RequestParam Double amount) {
        return ResponseEntity.ok(cardService.rechargeBalance(amount));
    }



    //-------------------------------DELETE--------------------------------//

    /**
     * Elimina la tarjeta asociada al usuario autenticado.
     *
     * @return ResponseEntity con estado 204 No Content si la eliminación fue exitosa.
     */

    @DeleteMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> delete() {
        cardService.deleteFromAuthenticatedUser();
        return ResponseEntity.noContent().build();
    }
}

