package com.example.springboot3.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.springboot3.dtos.ProductRecordDTO;
import com.example.springboot3.models.ProductModel;
import com.example.springboot3.repositories.ProductRepository;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

	private static final String PRODUCT_NOT_FOUND = "Product not found.";
	private static final String PRODUCT_DELETED_SUCCESSFULLY = "Product deleted successfully.";

	@Autowired
	ProductRepository productRepository;

	@PostMapping("/products")
	public ResponseEntity<ProductModel> saveProduct(
			@RequestBody @Valid ProductRecordDTO productRecordDTO
	) {
		var productModel = new ProductModel();
		BeanUtils.copyProperties(productRecordDTO, productModel);
		return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
	}

	@GetMapping("/products")
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

	@GetMapping("/products/{id}")
	public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> productModelOptional = productRepository.findById(id);
		return productModelOptional.<ResponseEntity<Object>>map(
				productModel ->
						ResponseEntity.status(HttpStatus.OK).body(
								productModel.add(
										linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Product List"))
						)).orElseGet(
				() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUCT_NOT_FOUND));
	}

	@PutMapping("/products/{id}")
	public ResponseEntity<Object> updateProduct(
			@PathVariable(value = "id") UUID id,
			@RequestBody @Valid ProductRecordDTO productRecordDTO
	) {
		Optional<ProductModel> productModelOptional = productRepository.findById(id);
		if (productModelOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUCT_NOT_FOUND);
		}

		var productModel = productModelOptional.get();
		BeanUtils.copyProperties(productRecordDTO, productModel);
		return ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable(value = "id") UUID id) {
		Optional<ProductModel> productModelOptional = productRepository.findById(id);
		if (productModelOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(PRODUCT_NOT_FOUND);
		}

		productRepository.delete(productModelOptional.get());

		return ResponseEntity.status(HttpStatus.OK).body(PRODUCT_DELETED_SUCCESSFULLY);
	}
}
