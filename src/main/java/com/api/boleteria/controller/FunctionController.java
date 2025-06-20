package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.FunctionDetailDTO;
import com.api.boleteria.dto.list.FunctionListDTO;
import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.model.enums.ScreenType;
import com.api.boleteria.service.FunctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de funciones (proyecciones de películas).
 *
 * Permite crear, consultar, actualizar y eliminar funciones, así como obtener
 * funciones disponibles por película o por tipo de pantalla.
 *
 * La mayoría de las operaciones requieren rol ADMIN, aunque la consulta está permitida
 * también para usuarios con rol CLIENT.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/functions")
public class FunctionController {
    private final FunctionService functionService;


    //-------------------------------CREATE--------------------------------//

    /**
     * Crea una o varias funciones nuevas.
     *
     * @param entities Lista de DTOs con los datos necesarios para crear cada función.
     * @return ResponseEntity con la lista de detalles de las funciones creadas.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FunctionDetailDTO>> create(@Valid @RequestBody List<FunctionRequestDTO> entities) {
        return ResponseEntity.ok(functionService.createAll(entities));
    }


    //-------------------------------GET--------------------------------//

    /**
     * Obtiene la lista de todas las funciones.
     *
     * @return ResponseEntity con una lista de funciones o 204 No Content si no hay funciones.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<FunctionListDTO>> getAll() {
        List<FunctionListDTO> list = functionService.findAll();
        if (list.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene el detalle de una función específica por su ID.
     *
     * @param id Identificador de la función.
     * @return ResponseEntity con el detalle de la función.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<FunctionDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(functionService.findById(id));
    }

    /**
     * Obtiene la lista de funciones disponibles para una película específica,
     * considerando únicamente aquellas con capacidad disponible.
     * <p>
     * Si no se encuentran funciones disponibles para la película indicada,
     * se devuelve una respuesta con código 204 (No Content).
     *
     * @param movieId Identificador de la película.
     * @return ResponseEntity con la lista de funciones disponibles o 204 si está vacía.
     */
    @GetMapping("/disponibles/{movieId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<FunctionListDTO>> getAvailableFunctionsPerMovie(@PathVariable Long movieId) {
        List<FunctionListDTO> functions = functionService.findByMovieIdAndAvailableCapacity(movieId);

        if (functions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(functions);
    }

    /**
     * Obtiene la lista de funciones filtradas por tipo de pantalla.
     * <p>
     * Si no se encuentran funciones para el tipo de pantalla indicado,
     * se devuelve una respuesta con estado 204 (No Content).
     *
     * @param screenType Tipo de pantalla para filtrar las funciones.
     * @return ResponseEntity con la lista de funciones que coinciden con el tipo de pantalla,
     * o estado 204 si no hay resultados.
     */
    @GetMapping("/tipo-pantalla/{screenType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<FunctionListDTO>> getByScreenType(@PathVariable ScreenType screenType) {
        List<FunctionListDTO> functions = functionService.findByScreenType(screenType);

        if (functions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(functions);
    }


        //-------------------------------UPDATE--------------------------------//

        /**
         * Actualiza una función por su ID.
         *
         * @param id Identificador de la función a actualizar.
         * @param entity DTO con los nuevos datos de la función.
         * @return ResponseEntity con el detalle actualizado de la función.
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<FunctionDetailDTO> update (@PathVariable Long id, @Valid @RequestBody FunctionRequestDTO
        entity){
            return ResponseEntity.ok(functionService.updateById(id, entity));
        }


        //-------------------------------DELETE--------------------------------//

        /**
         * Elimina una función por su ID.
         *
         * @param id Identificador de la función a eliminar.
         * @return ResponseEntity con mensaje confirmando la eliminación.
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<String> delete(@PathVariable Long id){
            functionService.deleteById(id);
            return ResponseEntity.ok("Función eliminada correctamente.");
        }
    }

