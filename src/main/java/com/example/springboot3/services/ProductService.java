package com.example.springboot3.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.springboot3.controllers.ProductController;
import com.example.springboot3.dtos.ProductRecordDTO;
import com.example.springboot3.models.ProductModel;
import com.example.springboot3.repositories.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private static final String PRODUCT_NOT_FOUND = "Product not found.";
  private static final String PRODUCT_DELETED_SUCCESSFULLY = "Product deleted successfully.";
  private static final String PRODUCT_LIST = "Product List";

  @Autowired
  ProductRepository productRepository;

  public ResponseEntity<ProductModel> save(final ProductRecordDTO productRecordDTO) {
    var productModel = new ProductModel();
    BeanUtils.copyProperties(productRecordDTO, productModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
  }

  public ResponseEntity<List<ProductModel>> getAllProducts() {
    List<ProductModel> productList = productRepository.findAll();

    if (!productList.isEmpty()) {
      for (ProductModel productModel : productList) {
        productModel.add(
            linkTo(
                methodOn(ProductController.class).getOneProduct(productModel.getUuid())
            ).withSelfRel()
        );
      }
    }

    return ResponseEntity.status(HttpStatus.OK).body(productList);
  }

  public ResponseEntity<Object> getOneProduct(final UUID id) {
    Optional<ProductModel> productModelOptional = productRepository.findById(id);
    return productModelOptional.<ResponseEntity<Object>>map(
        productModel ->
            ResponseEntity.status(HttpStatus.OK).body(
                productModel.add(
                    linkTo(methodOn(ProductController.class).getAllProducts())
                        .withRel(PRODUCT_LIST))
            )).orElseGet(
        () -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUCT_NOT_FOUND));
  }

  public ResponseEntity<Object> updateProduct(final UUID id,
      final ProductRecordDTO productRecordDTO) {
    Optional<ProductModel> productModelOptional = productRepository.findById(id);
    if (productModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUCT_NOT_FOUND);
    }

    var productModel = productModelOptional.get();
    BeanUtils.copyProperties(productRecordDTO, productModel);
    return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
  }

  public ResponseEntity<Object> deleteProduct(final UUID id) {
    Optional<ProductModel> productModelOptional = productRepository.findById(id);
    if (productModelOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUCT_NOT_FOUND);
    }

    productRepository.delete(productModelOptional.get());

    return ResponseEntity.status(HttpStatus.OK).body(PRODUCT_DELETED_SUCCESSFULLY);
  }
}
