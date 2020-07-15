package com.reljicd.controller;

import com.reljicd.exception.NotEnoughProductsInStockException;
import com.reljicd.model.Form;
import com.reljicd.service.FormService;
import com.reljicd.service.ProductService;
import com.reljicd.service.ShoppingCartService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    private final ProductService productService;
    private final FormService formService;

    @Autowired
    public ShoppingCartController(ShoppingCartService shoppingCartService, ProductService productService, FormService formService) {
        this.shoppingCartService = shoppingCartService;
        this.productService = productService;
        this.formService = formService;
    }

    @GetMapping("/shoppingCart")
    public ModelAndView shoppingCart() {
        ModelAndView modelAndView = new ModelAndView("/shoppingCart");
        modelAndView.addObject("products", shoppingCartService.getProductsInCart());
        modelAndView.addObject("total", shoppingCartService.getTotal().toString());
        modelAndView.addObject("forms", shoppingCartService.getFormsInCart());
        modelAndView.addObject("comment", "blank");
        return modelAndView;
    }

    @GetMapping("/shoppingCart/addProduct/{productId}")
    public ModelAndView addProductToCart(@PathVariable("productId") Long productId) {
        productService.findById(productId).ifPresent(shoppingCartService::addProduct);
        return shoppingCart();
    }
    
    @GetMapping("/shoppingCart/processForm/{formId}")
    public ModelAndView addFormToCart(@PathVariable("formId") Long formId) {
        formService.findById(formId).ifPresent(shoppingCartService::addForm);
        return shoppingCart();
    }
    
    @GetMapping("/shoppingCart/approveForm/{formId}/{comment}")
    public ModelAndView approveForm(@PathVariable("formId") Long formId, @PathVariable("comment") String comment) {
        Optional<Form> optionalForm = formService.findById(formId);
        optionalForm.ifPresent(shoppingCartService::approveForm);
        optionalForm.ifPresent(form -> form.setComment(comment) );
        return shoppingCart();
    }
    
    @GetMapping("/shoppingCart/denyForm/{formId}")
    public ModelAndView denyForm(@PathVariable("formId") Long formId) {
        formService.findById(formId).ifPresent(shoppingCartService::denyForm);
        return shoppingCart();
    }

    @GetMapping("/shoppingCart/removeProduct/{productId}")
    public ModelAndView removeProductFromCart(@PathVariable("productId") Long productId) {
        productService.findById(productId).ifPresent(shoppingCartService::removeProduct);
        return shoppingCart();
    }

    @GetMapping("/shoppingCart/checkout")
    public ModelAndView checkout() {
        try {
            shoppingCartService.checkout();
        } catch (NotEnoughProductsInStockException e) {
            return shoppingCart().addObject("outOfStockMessage", e.getMessage());
        }
        return shoppingCart();
    }
}
