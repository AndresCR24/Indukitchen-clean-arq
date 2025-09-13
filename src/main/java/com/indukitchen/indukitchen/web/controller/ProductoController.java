package com.indukitchen.indukitchen.web.controller;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.ProductoDto;
import com.indukitchen.indukitchen.domain.dto.SuggestProductDto;
import com.indukitchen.indukitchen.domain.dto.UpdateClienteDto;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
import com.indukitchen.indukitchen.domain.service.ProductoService;
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
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final IndukitchenAiService indukitchenAiService;

    public ProductoController(ProductoService productoService, IndukitchenAiService indukitchenAiService) {
        this.productoService = productoService;
        this.indukitchenAiService = indukitchenAiService;
    }


    @GetMapping()
    public ResponseEntity<List<ProductoDto>> getAll() {

        return ResponseEntity.ok(this.productoService.getAll());
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
    public ResponseEntity<ProductoDto> getById(@Parameter(description = "Identificador del producto") @PathVariable long id) {
        ProductoDto productoDto = this.productoService.getById(id);

        // Cuando es nulo
        if (productoDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(productoDto);
    }

    //POST
    @PostMapping()
    public ResponseEntity<ProductoDto> add(@RequestBody ProductoDto productoDto) {
        //Otra logica
        //MovieDto movieDtoResponse = this.movieService.add(movieDto);
        //return ResponseEntity.status(HttpStatus.CREATED).body(movieDtoResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productoService.add(productoDto));
    }

    @PostMapping("/suggest")
    public ResponseEntity<String> generateProductSuggestion(@RequestBody SuggestProductDto suggestProductDto) {
        return ResponseEntity.ok(this.indukitchenAiService.generateProductSuggestion(suggestProductDto.userPreferences()));
    }

    //PUT
//    @PutMapping("/{id}")
//    public ResponseEntity<ClienteDto> update(@PathVariable long id, @RequestBody @Valid UpdateClienteDto updateClienteDto) {
//        return ResponseEntity.ok(this.productoService.(id, updateMovieDto));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        this.productoService.delete(id);
        return ResponseEntity.ok().build();
    }
}

