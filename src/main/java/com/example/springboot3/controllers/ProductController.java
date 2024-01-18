package com.example.springboot3.controllers;

import com.example.springboot3.dtos.ProductRecordDTO;
import com.example.springboot3.models.ProductModel;
import com.example.springboot3.services.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

  @Autowired
  ProductService productService;

  @PostMapping("/products")
  public ResponseEntity<ProductModel> saveProduct(
      @RequestBody @Valid ProductRecordDTO productRecordDTO
  ) {
    return productService.save(productRecordDTO);
  }

  @GetMapping("/products")
  public ResponseEntity<List<ProductModel>> getAllProducts() {
    return productService.getAllProducts();
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
    return productService.getOneProduct(id);
  }

  @PutMapping("/products/{id}")
  public ResponseEntity<Object> updateProduct(
      @PathVariable(value = "id") UUID id,
      @RequestBody @Valid ProductRecordDTO productRecordDTO
  ) {
    return productService.updateProduct(id, productRecordDTO);
  }

  @DeleteMapping("/products/{id}")
  public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
    return productService.deleteProduct(id);
  }
}
