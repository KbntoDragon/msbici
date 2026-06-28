package com.inventario.ms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.ms.DTO.ProductoDTO;
import com.inventario.ms.model.Producto;
import com.inventario.ms.services.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Gestión de productos del inventario")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    //Listar
    @Operation(summary = "Listar todos los productos",
               description = "Devuelve la lista completa de productos. Responde 204 si no hay ninguno.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos encontrada"),
        @ApiResponse(responseCode = "204", description = "No hay productos registrados")
    })
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        List<ProductoDTO> productos = productoService.obtenerProductos();
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }
    //Buscar id
    @Operation(summary = "Buscar producto por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no existe")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> buscarProductoPorId(@PathVariable Integer id) {
        try {
            ProductoDTO producto = productoService.obtenerProductoDTOPorId(id);
            return new ResponseEntity<>(producto, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    //Buscar por codigobarra
    @Operation(summary = "Buscar productos por código de barras")
    @GetMapping("/codigo/{codigoBarra}")
    public ResponseEntity<List<ProductoDTO>> buscarPorCodigoBarra(@PathVariable String codigoBarra) {
        try{
            List<ProductoDTO> productos = productoService.buscarPorCodigoDeBarraDTO(codigoBarra);
            return new ResponseEntity<>(productos,HttpStatus.OK);
        } catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }
    //guardar producto
    @Operation(summary = "Crear un producto",
               description = "Valida que el precio sea mayor a 0 y el stock no sea negativo.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (precio o stock)")
    })
    @PostMapping
    public ResponseEntity<Producto> guardarProducto(@RequestBody Producto producto) {
       try {
         Producto producto2 = productoService.guardarProducto(producto);
         return new ResponseEntity<>(producto2, HttpStatus.CREATED);
       } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
       }
    }
    //eliminar por id
    @Operation(summary = "Eliminar producto por id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarProducto(@PathVariable Integer id) {
        String resultado = productoService.eliminarProducto(id);
        if (resultado.contains("exito")) {
            return new ResponseEntity<>(resultado,HttpStatus.OK);
        }else{
            return new ResponseEntity<>(resultado, HttpStatus.NOT_FOUND);
        }
    }

    //buscar por nombre
    @Operation(summary = "Buscar productos por nombre (contiene, ignora mayúsculas)")
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(@RequestParam String nombre){
        List<ProductoDTO> productos = productoService.buscarPorNombreDTO(nombre);
        if(productos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    //sin Stock
    @Operation(summary = "Listar productos sin stock")
    @GetMapping("/sin-stock")
    public ResponseEntity<List<ProductoDTO>> sinStock() {
        List<ProductoDTO> productos = productoService.obtenerProductoSinStockDTO();
        if (productos == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    //actualizar
    @Operation(summary = "Actualizar un producto existente")
    @PatchMapping("/{id}")
    public ResponseEntity<Producto> editarProducto(@PathVariable Integer id, @RequestBody Producto producto){
        try {
            Producto editado = productoService.guardarProducto(producto);
            return new ResponseEntity<>(editado, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
