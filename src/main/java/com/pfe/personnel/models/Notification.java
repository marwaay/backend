package com.pfe.personnel.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "sender_id")
    private Long sender_id;
    @Column(name = "recipient_id")
    private Long recipient_id;
    @Column(name = "subject")
    private String subject;
    @Lob
    @Column(name = "content")
    private String content;
    @Column(name = "sent_at")
    private Timestamp sent_at;
    @Column(name = "lu")
    private  Integer lu;
}
