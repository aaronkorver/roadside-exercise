package com.example.geico.roadside.stateMachine;


import com.example.geico.roadside.postgis.AssistantModel;
import jakarta.persistence.*;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name="assistant_transactions")
public class AssistantTransaction {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="assistant_id", nullable = false)
    AssistantModel assistant;

    @Id
    @GeneratedValue(generator = "UUID")
    UUID id;

    UUID customerId;

    @Column(name="to_state")
    @Enumerated(EnumType.STRING)
    AssistantState toState;
    boolean mostRecent;


    Timestamp createdAt;
    Timestamp updatedAt;

    public AssistantTransaction() {
    }

    public AssistantTransaction(AssistantModel assistant, UUID customerId, AssistantState toState, boolean mostRecent) {
        this.assistant = assistant;
        this.customerId = customerId;
        this.toState = toState;
        this.mostRecent = mostRecent;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = Timestamp.valueOf(now);
        this.updatedAt = Timestamp.valueOf(now);
    }

    public AssistantModel getAssistant() {
        return assistant;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public AssistantState getToState() {
        return toState;
    }

    public boolean isMostRecent() {
        return mostRecent;
    }

    public void setMostRecent(boolean mostRecent) {
        this.mostRecent = mostRecent;
    }


    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
