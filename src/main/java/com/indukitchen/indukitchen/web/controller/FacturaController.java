package com.indukitchen.indukitchen.web.controller;

import com.indukitchen.indukitchen.domain.dto.FacturaDto;
import com.indukitchen.indukitchen.domain.service.FacturaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.*;
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

    /** Descarga / visualiza el PDF de factura */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getFacturaPdf(@PathVariable long id) {
        var baos = facturaService.generateFacturaPdf(id);
        byte[] bytes = baos.toByteArray();
        if (bytes.length == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline().filename("factura-" + id + ".pdf").build());
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }

    /** Genera el PDF y lo envía por email como adjunto */
//    @PostMapping("/{id}/{emai}")
//    public ResponseEntity<Void> sendFacturaPdfByEmail(@PathVariable long id, @RequestBody EmailRequestDto emailRequestDto) throws MessagingException {
//        facturaService.emailFacturaPdf(id, emailRequestDto.to(), emailRequestDto.subject(), emailRequestDto.text());
//        // 202 Accepted: aceptado para envío
//        return ResponseEntity.accepted().build();
//    }

    @PostMapping("/{id}/email")
    public ResponseEntity<Void> sendFacturaEmail(@PathVariable long id) {
        facturaService.generarEnviarFactura(id); // usa el método que ya tienes en FacturaService
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

}