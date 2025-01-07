package com.example.OrderService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // Tablo adını değiştirdik
@Data // Getter, Setter, toString, equals ve hashCode oluşturur
@NoArgsConstructor // Parametresiz constructor
@AllArgsConstructor // Tüm alanlar için constructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime orderDate;
    private Double totalAmount;
    private Integer status; // Integer olarak değiştirildi

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>(); // Boş listeyle başlatıldı
}
