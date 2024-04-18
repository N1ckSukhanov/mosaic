package com.example.mosaica.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Builder
@Table(name = "image_block")
@AllArgsConstructor
public class ImageBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "access_code_id", insertable = false, updatable = false)
    private AccessCode accessCode;

    @Column(nullable = false)
    private Integer blockNumber;

    @Column(nullable = false)
    private Integer height;

    @Column(nullable = false)
    private Integer width;

    @ElementCollection
    @OrderColumn
    private List<Integer> blockColors;
}
