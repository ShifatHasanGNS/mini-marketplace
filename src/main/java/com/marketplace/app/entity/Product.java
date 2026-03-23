package com.marketplace.app.entity; // Package where entity classes are stored

import jakarta.persistence.*; // Import JPA annotations for mapping Java objects to database tables

// Mark this class as a JPA entity
@Entity
// Specify the database table name for this entity
@Table(name = "product")
public class Product {

    // Primary key of the product table
    @Id
    // Auto-generate the ID value using database identity strategy
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Product name
    private String name;

    // Product price
    private Double price;

    // Product origin (e.g., country or location)
    private String origin;

    // URL or path to product image
    private String pic;

    // Seller who owns this product
    private String sellerName;

    // Default constructor required by JPA
    public Product() {}

    // Constructor to initialize all fields
    public Product(
        String name,
        Double price,
        String origin,
        String pic,
        String sellerName
    ) {
        this.name = name;
        this.price = price;
        this.origin = origin;
        this.pic = pic;
        this.sellerName = sellerName;
    }

    // Getter for product ID
    public Long getId() {
        return id;
    }

    // Setter for product ID
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for product name
    public String getName() {
        return name;
    }

    // Setter for product name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for product price
    public Double getPrice() {
        return price;
    }

    // Setter for product price
    public void setPrice(Double price) {
        this.price = price;
    }

    // Getter for product origin
    public String getOrigin() {
        return origin;
    }

    // Setter for product origin
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    // Getter for product image path/URL
    public String getPic() {
        return pic;
    }

    // Setter for product image path/URL
    public void setPic(String pic) {
        this.pic = pic;
    }

    // Getter for seller name
    public String getSellerName() {
        return sellerName;
    }

    // Setter for seller name
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }
}