package com.finalproject.storemanagementproject.controllers;

import com.finalproject.storemanagementproject.models.APIResponse;
import com.finalproject.storemanagementproject.models.OrderProduct;
import com.finalproject.storemanagementproject.models.Product;
import com.finalproject.storemanagementproject.services.OrderProductService;
import com.finalproject.storemanagementproject.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    OrderProductService orderProductService;

    @GetMapping("")
    public ResponseEntity<APIResponse<Product>> getProducts(@RequestParam(required = false) String text) {
        List<Product> products;

        if (text != null && !text.isEmpty())
            products = productService.findProductByName(text);
        else products = productService.getAllProducts();

        if (products == null) {
            return ResponseEntity.ok(
                    new APIResponse<>(HttpStatus.NOT_FOUND.value(), "Not Found", Collections.emptyList()));
        }

        return ResponseEntity
                .ok(new APIResponse<>(HttpStatus.OK.value(), "Success", products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<Product>> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(id);

        int HTTP_CODE = HttpStatus.OK.value();
        String message = "Success";

        if (product == null) {
            HTTP_CODE = HttpStatus.NOT_FOUND.value();
            message = "Not found product";
        }

        return ResponseEntity.ok(new APIResponse<>(HTTP_CODE, message, Collections.singletonList(product)));
    }

    @PostMapping("/create")
    public ResponseEntity<APIResponse<Product>> createProduct(@RequestBody Product product) {
        Integer HTTP_CODE = HttpStatus.OK.value();
        String message = "Create Success";

        if (!productService.findByBarCode(product.getBarcode()).isEmpty()) {
            HTTP_CODE = HttpStatus.BAD_REQUEST.value();
            message = "Barcode is already in use";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HTTP_CODE, message, Collections.singletonList(null)));
        }

        product.setCreatedAt(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(+7))));
        product.setUpdatedAt(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(+7))));

        Product savedProduct = productService.saveProduct(product);
        return ResponseEntity.ok(new APIResponse<>(HTTP_CODE, message,
                Collections.singletonList(savedProduct)));
    }

    @PostMapping("update/{id}")
    public ResponseEntity<APIResponse<Product>> updateProduct(@PathVariable String id, @RequestBody Product updatedProduct) {
        Product product = productService.getProductById(id);

        Integer HTTP_CODE = HttpStatus.OK.value();
        String message = "Update Success";

        if (productService.findByBarCode(updatedProduct.getBarcode()).size() == 1 && !product.getBarcode().equals(updatedProduct.getBarcode())) {
            HTTP_CODE = HttpStatus.BAD_REQUEST.value();
            message = "Barcode is already in use";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HTTP_CODE, message, Collections.singletonList(null)));
        }

        if (product != null) {
            product.setName(updatedProduct.getName());
            product.setCategory(updatedProduct.getCategory());
            product.setRetailPrice(updatedProduct.getRetailPrice());
            product.setImportPrice(updatedProduct.getImportPrice());
            product.setBarcode(updatedProduct.getBarcode());
            product.setQuantity(updatedProduct.getQuantity());
            product.setIllustrator(updatedProduct.getIllustrator());

            product.setUpdatedAt(Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(+7))));
            productService.saveProduct(product);
            return ResponseEntity.ok(new APIResponse<>(HTTP_CODE, message, Collections.singletonList(product)));
        }

        HTTP_CODE = HttpStatus.NOT_FOUND.value();
        message = "Not found product";

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HTTP_CODE, message, Collections.singletonList(null)));
    }

    @PostMapping("delete/{id}")
    public ResponseEntity<APIResponse<Product>> deleteProduct(@PathVariable String id) {
        try {
            Product product = productService.getProductById(id);
            if (product == null)
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new APIResponse<>(HttpStatus.NOT_FOUND.value(), "Not found product", Collections.emptyList()));

            List<OrderProduct> orderProducts = orderProductService.getOrderProductsByPid(id);

            if (orderProducts != null && !orderProducts.isEmpty())
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Product is in use", Collections.emptyList()));

            productService.deleteProduct(id);
            return ResponseEntity.ok(new APIResponse<>(HttpStatus.OK.value(), "Delete Success", Collections.emptyList()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new APIResponse<>(HttpStatus.BAD_REQUEST.value(), "Delete Failed", Collections.emptyList()));
        }
    }
    
    // Get total product
    @GetMapping("/total")
    public ResponseEntity<APIResponse<Long>> getTotalProducts() {
    	long total = productService.getTotalProducts();
    	return ResponseEntity.ok(new APIResponse<Long>(HttpStatus.OK.value(), "Success", Collections.singletonList(total)));
    }
}
