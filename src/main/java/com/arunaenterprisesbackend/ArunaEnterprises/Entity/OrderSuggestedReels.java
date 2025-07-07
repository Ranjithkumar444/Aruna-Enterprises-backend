package com.arunaenterprisesbackend.ArunaEnterprises.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_suggested_reels")
@Getter
@Setter
@NoArgsConstructor
public class OrderSuggestedReels {
    @Id @GeneratedValue Long id;

    @OneToOne @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ElementCollection @CollectionTable(name = "order_suggested_top_reels", joinColumns = @JoinColumn(name = "suggestion_id"))
    private List<SuggestedReelItem> topReels = new ArrayList<>();

    @ElementCollection @CollectionTable(name = "order_suggested_bottom_reels", joinColumns = @JoinColumn(name = "suggestion_id"))
    private List<SuggestedReelItem> bottomReels = new ArrayList<>();

    @ElementCollection @CollectionTable(name = "order_suggested_flute_reels", joinColumns = @JoinColumn(name = "suggestion_id"))
    private List<SuggestedReelItem> fluteReels = new ArrayList<>();

    @CreationTimestamp
    private ZonedDateTime createdAt;
    private double usedDeckle;
}