package com.indukitchen.indukitchen.web.controller;

import com.indukitchen.indukitchen.domain.dto.ClienteDto;
import com.indukitchen.indukitchen.domain.dto.SuggestClienteDto;
import com.indukitchen.indukitchen.domain.dto.update.UpdateClienteDto;
import com.indukitchen.indukitchen.domain.service.ClienteService;
import com.indukitchen.indukitchen.domain.service.IndukitchenAiService;
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
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final IndukitchenAiService indukitchenAiService;

    public ClienteController(ClienteService clienteService, IndukitchenAiService indukitchenAiService) {
        this.clienteService = clienteService;
        this.indukitchenAiService = indukitchenAiService;
    }


    @GetMapping()
    public ResponseEntity<List<ClienteDto>> getAll() {

        return ResponseEntity.ok(this.clienteService.getAllClientes());
    }

    @GetMapping("/{cedula}")
    @Operation(
            summary = "Get a product by id \n se obtiene una producto por su id",
            description = "Retorna una producto",
            responses = {
                    @ApiResponse(responseCode = "200", description = "producto encontrada"),
                    @ApiResponse(responseCode = "404", description = "producto no encontrada", content = @Content)
            }
    )
    public ResponseEntity<ClienteDto> getById(@Parameter(description = "Identificador del producto") @PathVariable String cedula) {
        ClienteDto clienteDto = this.clienteService.getById(cedula);

        // Cuando es nulo
        if (clienteDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(clienteDto);
    }

    //POST
    @PostMapping()
    public ResponseEntity<ClienteDto> add(@RequestBody ClienteDto clienteDto) {
        //Otra logica
        //MovieDto movieDtoResponse = this.movieService.add(movieDto);
        //return ResponseEntity.status(HttpStatus.CREATED).body(movieDtoResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.clienteService.add(clienteDto));
    }

    @PostMapping("/suggest")
    public ResponseEntity<String> generateClienteSuggestion(@RequestBody SuggestClienteDto suggestCliente) {
        return ResponseEntity.ok(this.indukitchenAiService.generateClienteSuggestion(suggestCliente.userPreferences()));
    }

    //PUT
    @PutMapping("/{cedula}")
    public ResponseEntity<ClienteDto> update(@PathVariable String cedula, @RequestBody @Valid UpdateClienteDto updateClienteDto) {
        return ResponseEntity.ok(this.clienteService.update(cedula, updateClienteDto));
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> delete(@PathVariable String cedula) {
        this.clienteService.delete(cedula);
        return ResponseEntity.ok().build();
    }
}


