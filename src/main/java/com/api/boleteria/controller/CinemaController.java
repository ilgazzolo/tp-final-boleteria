package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.CinemaDetailDTO;
import com.api.boleteria.dto.list.CinemaListDTO;
import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.model.enums.ScreenType;
import com.api.boleteria.service.CinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de cines.
 *
 * Permite realizar operaciones CRUD sobre los cines, así como consultas
 * filtradas por tipo de pantalla, estado habilitado y capacidad de asientos.
 *
 * La mayoría de las operaciones están restringidas a usuarios con rol ADMIN.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cinemas")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;


    //-------------------------------CREATE--------------------------------//

    /**
     * Crea una nueva sala.
     *
     * @param entity DTO con la información de la sala a crear.
     * @return ResponseEntity con el detalle de la sala creada.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CinemaDetailDTO>> create(@Valid @RequestBody List<@Valid CinemaRequestDTO> entity){
        return ResponseEntity.ok(cinemaService.saveAll(entity));
    }



    //-------------------------------GET--------------------------------//

    /**
     * Obtiene la lista de todas las salas.
     *
     * @return ResponseEntity con una lista de DTOs de salas o un 204 No Content si no hay salas.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<CinemaListDTO>> getList(){
        List<CinemaListDTO> list = cinemaService.findAll();

        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(list);
    }

       /**
     * Obtiene el detalle de una sala específica por su ID.
     *
     * @param id Identificador de la sala.
     * @return ResponseEntity con el detalle de la sala solicitada.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<CinemaDetailDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(cinemaService.findById(id));
    }


    /**
     * Obtiene la lista de salas filtradas por tipo de pantalla.
     *
     * @param screenType Tipo de pantalla para filtrar.
     * @return ResponseEntity con la lista de salas o 204 No Content si no hay coincidencias.
     */
    @GetMapping("/ScreenType/{screenType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<CinemaListDTO>> getByScreenType(@PathVariable ScreenType screenType){
        List<CinemaListDTO> list = cinemaService.findByScreenType(screenType);

        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }


    /**
     * Obtiene la lista de salas filtradas por estado habilitado de la sala.
     *
     * @param enabled Estado habilitado para filtrar (true o false).
     * @return ResponseEntity con la lista de salas o 204 No Content si no hay coincidencias.
     */
    @GetMapping("/Enabled/{enabled}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<CinemaListDTO>> getByEnabledRoom(@PathVariable boolean enabled){
        List<CinemaListDTO> list = cinemaService.findByEnabledRoom(enabled);

        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene la lista de salas filtradas por capacidad de asientos.
     *
     * @param capacity Capacidad de asientos para filtrar.
     * @return ResponseEntity con la lista de salas o 204 No Content si no hay coincidencias.
     */
    @GetMapping("/Capacity/{capacity}")
    public ResponseEntity<List<CinemaListDTO>> getBySeatCapacity(@PathVariable Integer capacity){
        List<CinemaListDTO> list = cinemaService.findBySeatCapacity(capacity);

        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }


    //-------------------------------UPDATE--------------------------------//

    /**
     * Actualiza la información de una sala por su ID.
     *
     * @param id Identificador de la sala a actualizar.
     * @param entity DTO con la nueva información para la sala.
     * @return ResponseEntity con el detalle actualizado de la sala.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CinemaDetailDTO> update(@PathVariable Long id, @Valid @RequestBody CinemaRequestDTO entity){
        return ResponseEntity.ok(cinemaService.updateById(id, entity));
    }



    //-------------------------------DELETE--------------------------------//

    /**
     * Elimina una sala por su ID.
     *
     * @param id Identificador de la sala a eliminar.
     * @return ResponseEntity con mensaje confirmando la eliminación.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        cinemaService.deleteById(id);
        return ResponseEntity.ok("Sala eliminada correctamente.");
    }

}
