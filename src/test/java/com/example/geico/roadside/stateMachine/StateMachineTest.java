package com.example.geico.roadside.stateMachine;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateMachineTest {

    StateMachine machine = new StateMachine();

    @Test
    void canTransitionFromAvailableToAssigned() {
        AssistantState available = AssistantState.AVAILABLE;
        AssistantState assigned = AssistantState.ASSIGNED;
        assertTrue(machine.canTransition(available,assigned), "Should be able to transition");
    }

    @Test
    void canTransitionFromAssignedToAvailable() {
        AssistantState available = AssistantState.AVAILABLE;
        AssistantState assigned = AssistantState.ASSIGNED;
        assertTrue(machine.canTransition(assigned,available), "Should be able to transition");
    }

    @Test
    void canNotTransitionFromSameState() {
        AssistantState available = AssistantState.AVAILABLE;
        AssistantState assigned = AssistantState.ASSIGNED;
        assertFalse(machine.canTransition(assigned,assigned), "Should NOT be able to transition");
    }
}