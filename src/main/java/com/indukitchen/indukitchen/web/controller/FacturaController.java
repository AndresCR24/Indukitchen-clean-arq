package com.indukitchen.indukitchen.web.controller;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.dto.SuggestProductDto;
import com.indukitchen.indukitchen.domain.service.FacturaService;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import com.indukitchen.indukitchen.domain.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar las facturas.
 */
@RestController
@RequestMapping("/facturas")
public class FacturaController {


    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping()
    public ResponseEntity<List<FacturaDto>> getAll() {

        return ResponseEntity.ok(this.facturaService.getAllFacturas());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a product by id \n se obtiene una producto por su id",
            description = "Retorna una producto",
            responses = {
                    @ApiResponse(responseCode = "200", description = "producto encontrada"),
                    @ApiResponse(responseCode = "404", description = "producto no encontrada", content = @Content)
            }
    )
    public ResponseEntity<FacturaDto> getById(@Parameter(description = "Identificador del producto") @PathVariable long id) {
        FacturaDto facturaDto = this.facturaService.getById(id);

        // Cuando es nulo
        if (facturaDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(facturaDto);
    }

    //POST
    @PostMapping()
    public ResponseEntity<FacturaDto> add(@RequestBody FacturaDto facturaDto) {
        //Otra logica
        //MovieDto movieDtoResponse = this.movieService.add(movieDto);
        //return ResponseEntity.status(HttpStatus.CREATED).body(movieDtoResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.facturaService.add(facturaDto));
    }

//    @PostMapping("/suggest")
//    public ResponseEntity<String> generateProductSuggestion(@RequestBody SuggestProductDto suggestProductDto) {
//        return ResponseEntity.ok(this.indukitchenAiService.generateProductSuggestion(suggestProductDto.userPreferences()));
//    }

    //PUT
//    @PutMapping("/{id}")
//    public ResponseEntity<ClienteDto> update(@PathVariable long id, @RequestBody @Valid UpdateClienteDto updateClienteDto) {
//        return ResponseEntity.ok(this.productoService.(id, updateMovieDto));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        this.facturaService.delete(id);
        return ResponseEntity.ok().build();
    }
}