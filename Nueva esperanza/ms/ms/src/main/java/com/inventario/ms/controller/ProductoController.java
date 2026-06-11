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

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    //Listar
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        List<ProductoDTO> productos = productoService.obtenerProductos();
        if (productos.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }
    //Buscar id
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
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscarPorNombre(@RequestParam String nombre){
        List<ProductoDTO> productos = productoService.buscarPorNombreDTO(nombre);
        if(productos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }
    
    //sin Stock
    @GetMapping("/sin-stock")
    public ResponseEntity<List<ProductoDTO>> sinStock() {
        List<ProductoDTO> productos = productoService.obtenerProductoSinStockDTO();
        if (productos == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    //actualizar
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
