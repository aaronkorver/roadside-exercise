package com.example.geico.roadside.stateMachine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssistantTransactionRepository extends JpaRepository<AssistantTransaction, UUID> {

    Optional<AssistantTransaction> findAssistantTransactionByAssistantIdAndMostRecentTrue(UUID assistantId);
}
