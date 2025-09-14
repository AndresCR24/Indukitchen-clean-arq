package com.indukitchen.indukitchen.web.controller;

import com.indukitchen.indukitchen.domain.dto.*;
import com.indukitchen.indukitchen.domain.dto.request.CreateCarritoRequestDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateCarritoDto;
import com.indukitchen.indukitchen.domain.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carritos")
public class CarritoController {

    private final CarritoService carritoService;
    //private final IndukitchenAiService indukitchenAiService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }


    @GetMapping()
    public ResponseEntity<List<CarritoDto>> getAll() {

        return ResponseEntity.ok(this.carritoService.getAllCarritos());
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
    public ResponseEntity<CarritoDto> getById(@Parameter(description = "Identificador del producto") @PathVariable long id) {
        CarritoDto carritoDto = this.carritoService.getById(id);

        // Cuando es nulo
        if (carritoDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carritoDto);
    }

    //POST
    @PostMapping()
    public ResponseEntity<CarritoDto> add(@RequestBody CreateCarritoRequestDto createCarritoRequestDto) {
        //Otra logica
        //MovieDto movieDtoResponse = this.movieService.add(movieDto);
        //return ResponseEntity.status(HttpStatus.CREATED).body(movieDtoResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.carritoService.add(createCarritoRequestDto));
    }

//    @PostMapping("/suggest")
//    public ResponseEntity<String> generateCarritoSuggestion(@RequestBody SuggestClienteDto suggestCliente) {
//        return ResponseEntity.ok(this.indukitchenAiService.generateClienteSuggestion(suggestCliente.userPreferences()));
//    }

    //PUT
    @PutMapping("/{id}")
    public ResponseEntity<CarritoDto> update(@PathVariable long id, @RequestBody @Valid UpdateCarritoDto updateCarritoDto) {
        return ResponseEntity.ok(this.carritoService.update(id, updateCarritoDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        this.carritoService.delete(id);
        return ResponseEntity.ok().build();
    }
}

