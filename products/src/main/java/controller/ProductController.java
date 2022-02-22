package controller;

import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    public final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        try {
            Product product = productService.getById(id);
            return new ResponseEntity<Product>(product, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Product>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(path = "/shop", produces = "application/json")
    public ResponseEntity<List<Product>> getProductsByName(@RequestParam(value = "name") String name) {
        try {
            List<Product> products = productService.getAllByName(name);
            if (products.isEmpty()) {
                return new ResponseEntity<List<Product>>(HttpStatus.NOT_FOUND);
            } else {
                return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<List<Product>>(HttpStatus.BAD_REQUEST);
        }
    }

}
