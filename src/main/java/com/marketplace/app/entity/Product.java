package com.marketplace.app.entity;

import jakarta.persistence.*;

/**
 * Product Entity
 * 
 * Represents a product in the Mini Marketplace system.
 * Stores product details including name, price, origin, image reference, and seller information.
 * Each product is mapped to a database table and can be managed by sellers.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Entity
@Table(name = "product")
public class Product {

    /**
     * Unique identifier for the product
     * Auto-generated primary key in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product
     */
    private String name;

    /**
     * Price of the product
     */
    private Double price;

    /**
     * Origin/source of the product (country or location)
     */
    private String origin;

    /**
     * Image URL or file path for product image
     */
    private String pic;

    /**
     * Name of the seller who owns this product
     */
    private String sellerName;

    /**
     * Default constructor required by JPA for entity instantiation
     */
    public Product() {
    }

    /**
     * Constructor to initialize all product fields
     * 
     * @param name        the product name
     * @param price       the product price
     * @param origin      the product origin
     * @param pic         the product image path
     * @param sellerName  the seller's username
     */
    public Product(String name, Double price, String origin, String pic, String sellerName) {
        this.name = name;
        this.price = price;
        this.origin = origin;
        this.pic = pic;
        this.sellerName = sellerName;
    }

    /**
     * Gets the product ID
     * 
     * @return the unique product identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the product ID
     * 
     * @param id the product identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the product name
     * 
     * @return the product name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the product name
     * 
     * @param name the product name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the product price
     * 
     * @return the product price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Sets the product price
     * 
     * @param price the product price to set
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * Gets the product origin
     * 
     * @return the product origin/source
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Sets the product origin
     * 
     * @param origin the product origin to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Gets the product image path
     * 
     * @return the image URL or file path
     */
    public String getPic() {
        return pic;
    }

    /**
     * Sets the product image path
     * 
     * @param pic the image URL or file path to set
     */
    public void setPic(String pic) {
        this.pic = pic;
    }

    /**
     * Gets the seller's username who owns this product
     * 
     * @return the seller name
     */
    public String getSellerName() {
        return sellerName;
    }

    /**
     * Sets the seller's username for this product
     * 
     * @param sellerName the seller name to set
     */
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
}